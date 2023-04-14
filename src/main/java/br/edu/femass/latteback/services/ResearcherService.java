package br.edu.femass.latteback.services;

import br.edu.femass.latteback.models.*;
import br.edu.femass.latteback.repositories.ArticleRepositoy;
import br.edu.femass.latteback.repositories.BookRepository;
import br.edu.femass.latteback.repositories.ResearcherCacheRepository;
import br.edu.femass.latteback.repositories.ResearcherRepository;
import br.edu.femass.latteback.services.interfaces.ResearcherServiceInterface;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResearcherService implements ResearcherServiceInterface {
    private final String RESUME_PATH = "src/main/resources/resume/";

    private final ResearcherRepository researcherRepository;
    private final InstituteService instituteService;
    private final ResearcherCacheRepository researcherCacheRepository;
    private final ArticleRepositoy articleRepositoy;
    private final BookRepository bookRepository;

    public ResearcherService(ResearcherRepository researcherRepository, InstituteService instituteService, ResearcherCacheRepository researcherCacheRepository, ArticleRepositoy articleRepositoy, BookRepository bookRepository) {
        this.researcherRepository = researcherRepository;
        this.instituteService = instituteService;
        this.researcherCacheRepository = researcherCacheRepository;
        this.articleRepositoy = articleRepositoy;
        this.bookRepository = bookRepository;
    }

    private static String getString(Document doc, String tagname, String name) {
        Node curriculoVitae = doc.getElementsByTagName(tagname).item(0);
        Element curriculoVitaeElement = (Element) curriculoVitae;
        return curriculoVitaeElement.getAttribute(name);
    }

    @Override
    @Transactional
    public Researcher save(String researcherIdNumber, UUID instituteID) {

        if (researcherIdNumber.isEmpty() || researcherIdNumber.isBlank()) {
            throw new IllegalArgumentException("Número do pesquisador não informado.");
        }
        if (researcherRepository.findFirstByResearcheridNumberContainsIgnoreCase(researcherIdNumber).isPresent()) {
            throw new IllegalArgumentException("O pesquisador já foi salvo.");
        }

        Optional<ResearcherCache> researcherCache = findResearcherOnCache(researcherIdNumber);
        String researcherFile;

        researcherFile = researcherCache.isPresent() ? researcherCache.get().getFileName() : searchFiles(researcherIdNumber).getFileName();

        Researcher researcher = getResearcherFromFile(researcherFile, instituteID);
        return researcher;
    }

    public Optional<ResearcherCache> findResearcherOnCache(String researcheridNumber) {
        return researcherCacheRepository.findFirstByResearcherIdNumber(researcheridNumber);
    }

    public ResearcherCache searchFiles(String researcherNumber) {

        try {
            File folder = new File(RESUME_PATH);
            File[] files = folder.listFiles();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            assert files != null;
            for (File file : files) {
                if (file.isFile() && file.getName().contains(".xml") && researcherCacheRepository.findFirstByResearcherIdNumber(researcherNumber).isEmpty()) {

                    Document doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();

                    String identifyingNumber = getString(doc, "CURRICULO-VITAE", "NUMERO-IDENTIFICADOR");
                    String fullName = getString(doc, "DADOS-GERAIS", "NOME-COMPLETO");

                    //Salva pesquisador em cache
                    ResearcherCache researcherCache = new ResearcherCache(fullName, identifyingNumber, file.getName());
                    researcherCacheRepository.save(researcherCache);

                    if (researcherNumber.equals(identifyingNumber)) return researcherCache;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Número identificador não encontrado.");
    }

    public Researcher getResearcherFromFile(String filename, UUID instituteID) {
        try {
            File file = new File(RESUME_PATH + "/" + filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            String identifyingNumber = getString(doc, "CURRICULO-VITAE", "NUMERO-IDENTIFICADOR");
            String fullName = getString(doc, "DADOS-GERAIS", "NOME-COMPLETO");
            String resume = getString(doc, "RESUMO-CV", "TEXTO-RESUMO-CV-RH");

            Researcher researcher = new Researcher(fullName, resume, resume);
            researcher.setResearcheridNumber(identifyingNumber);
            researcher.setInstitute(instituteService.getById(instituteID));

            researcherRepository.save(researcher);

            getResearcherArticles(researcher, doc);
            getResearcherBooks(researcher, doc);

            return researcher;
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new IllegalArgumentException("File not found.");
    }

    private Researcher getResearcherArticles(Researcher researcher, Document doc) {
        NodeList publishedArticles = doc.getElementsByTagName("ARTIGOS-PUBLICADOS");
        Element publishedArticlesElement = (Element) publishedArticles.item(0);
        NodeList articlesList = publishedArticlesElement.getElementsByTagName("ARTIGO-PUBLICADO");

        for (int i = 0; i < articlesList.getLength(); i++) {
            Element artigoPublicadoElement = (Element) articlesList.item(i);
            Element articleData = (Element) artigoPublicadoElement.getElementsByTagName("DADOS-BASICOS-DO-ARTIGO").item(0);
            String articleTitle = articleData.getAttribute("TITULO-DO-ARTIGO");
            String articleYear = articleData.getAttribute("ANO-DO-ARTIGO");

            Element articleElement = (Element) articlesList.item(i);
            Element detalheArtigo = (Element) articleElement.getElementsByTagName("DETALHAMENTO-DO-ARTIGO").item(0);
            String publisher = detalheArtigo.getAttribute("TITULO-DO-PERIODICO-OU-REVISTA");
            String volume = detalheArtigo.getAttribute("VOLUME");
            String paginas = detalheArtigo.getAttribute("PAGINA-INICIAL") + " - " + detalheArtigo.getAttribute("PAGINA-FINAL");

            NodeList autoresArtigo = artigoPublicadoElement.getElementsByTagName("AUTORES");
            ArrayList<String> autores = new ArrayList<>();
            for (int j = 0; j < autoresArtigo.getLength(); j++) {
                Element autorArtigo = (Element) autoresArtigo.item(j);
                String nomeAutor = autorArtigo.getAttribute("NOME-PARA-CITACAO");
                autores.add(nomeAutor);
            }

            Article article = new Article(articleTitle, publisher, volume, paginas, articleYear, autores, researcher);
            articleRepositoy.save(article);
        }
        return researcher;
    }

    private void getResearcherBooks(Researcher researcher, Document doc) {
        NodeList livrosLIsta = doc.getElementsByTagName("LIVRO-PUBLICADO-OU-ORGANIZADO");

        for (int i = 0; i < livrosLIsta.getLength(); i++) {
            Element livroPublicadoElement = (Element) livrosLIsta.item(i);
            Element dadosLivro = (Element) livroPublicadoElement.getElementsByTagName("DADOS-BASICOS-DO-LIVRO").item(0);
            String tituloLivro = dadosLivro.getAttribute("TITULO-DO-LIVRO");
            String anoLivro = dadosLivro.getAttribute("ANO");

            Element detalheLivro = (Element) livroPublicadoElement.getElementsByTagName("DETALHAMENTO-DO-LIVRO").item(0);
            String publisher = detalheLivro.getAttribute("NOME-DA-EDITORA");
            String volume = detalheLivro.getAttribute("NUMERO-DE-VOLUMES");
            String paginas = detalheLivro.getAttribute("NUMERO-DE-PAGINAS");

            NodeList autoresLivro = livroPublicadoElement.getElementsByTagName("AUTORES");
            ArrayList<String> autores = new ArrayList<>();
            for (int j = 0; j < autoresLivro.getLength(); j++) {
                Element autorArtigo = (Element) autoresLivro.item(j);
                String nomeAutor = autorArtigo.getAttribute("NOME-PARA-CITACAO");
                autores.add(nomeAutor);
            }
            Book book = new Book(tituloLivro, publisher, volume, paginas, anoLivro, autores, researcher);
            bookRepository.save(book);
        }
    }


    public List<Researcher> getAll() {
        return researcherRepository.findAll();
    }

    public Researcher getById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID procurado não pode ser nulo.");
        }

        var researcher = researcherRepository.findById(id);

        if (researcher.isEmpty()) {
            throw new IllegalArgumentException("Não há pesquisador cadastrado com esse id");
        }

        return researcher.get();
    }

    public void delete(UUID id) {
        var existResearcher = researcherRepository.existsById(id);

        if (!existResearcher) {
            throw new IllegalArgumentException("Não há pesquisador cadastrado com esse id");
        }

        researcherRepository.deleteById(id);

    }

    public Researcher update(UUID id, Researcher researcher) {
        if (researcher == null) throw new IllegalArgumentException("Objeto pesquisador nulo");

        assert researcher.getId() != null;
        var foundResearcher = researcherRepository.findById(id);

        if (foundResearcher.isEmpty()) throw new IllegalArgumentException("Pesquisador não encontrado");

        foundResearcher.get().setName(researcher.getName());
        foundResearcher.get().setEmail(researcher.getEmail());
        foundResearcher.get().setResearcheridNumber(researcher.getResearcheridNumber());
        foundResearcher.get().setResume(researcher.getResume());

        return researcherRepository.save(foundResearcher.get());
    }

    public Page<Researcher> AdvancedSearch(String name, String instituteAcronym, Pageable pageable){
        return researcherRepository.AdvancedSearch(name, instituteAcronym, pageable);
    }

}

