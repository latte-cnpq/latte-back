package br.edu.femass.latteback.services;
import br.edu.femass.latteback.models.*;
import br.edu.femass.latteback.models.graph.Collaboration;
import br.edu.femass.latteback.repositories.*;
import br.edu.femass.latteback.services.interfaces.ResearcherServiceInterface;
import br.edu.femass.latteback.utils.enums.ProductionType;
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
    private final ArticleRepository articleRepository;
    private final BookRepository bookRepository;
    private final CollaborationRepository collaborationRepository;

    public ResearcherService(ResearcherRepository researcherRepository, InstituteService instituteService, ResearcherCacheRepository researcherCacheRepository, ArticleRepository articleRepository, BookRepository bookRepository, CollaborationRepository collaborationRepository) {
        this.researcherRepository = researcherRepository;
        this.instituteService = instituteService;
        this.researcherCacheRepository = researcherCacheRepository;
        this.articleRepository = articleRepository;
        this.bookRepository = bookRepository;
        this.collaborationRepository = collaborationRepository;
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

        return getResearcherFromFile(researcherFile, instituteID);
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
    public void saveAll(UUID instituteId) {
        try {
            File folder = new File(RESUME_PATH);
            File[] files = folder.listFiles();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            assert files != null;
            for (File file : files) {
                System.out.println(file.getName());
                if (file.isFile() && file.getName().contains(".xml"))
                    getResearcherFromFile(file.getName(), instituteId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getResearcherArticles(Researcher researcher, Document doc) {
        NodeList publishedArticles = doc.getElementsByTagName("ARTIGOS-PUBLICADOS");
        Element publishedArticlesElement = (Element) publishedArticles.item(0);
        if (publishedArticlesElement != null) {
            NodeList articlesList = publishedArticlesElement.getElementsByTagName("ARTIGO-PUBLICADO");

            for (int i = 0; i < articlesList.getLength(); i++) {
                Element artigoPublicadoElement = (Element) articlesList.item(i);
                Element articleData = (Element) artigoPublicadoElement.getElementsByTagName("DADOS-BASICOS-DO-ARTIGO").item(0);
                String articleTitle = articleData.getAttribute("TITULO-DO-ARTIGO");
                String articleYear = articleData.getAttribute("ANO-DO-ARTIGO");

                Element articleElement = (Element) articlesList.item(i);
                Element articleDetails = (Element) articleElement.getElementsByTagName("DETALHAMENTO-DO-ARTIGO").item(0);
                String publisher = articleDetails.getAttribute("TITULO-DO-PERIODICO-OU-REVISTA");
                String volume = articleDetails.getAttribute("VOLUME");
                String pages = articleDetails.getAttribute("PAGINA-INICIAL") + " - " + articleDetails.getAttribute("PAGINA-FINAL");

                NodeList articleAuthors = artigoPublicadoElement.getElementsByTagName("AUTORES");
                ArrayList<String> authors = new ArrayList<>();
                for (int j = 0; j < articleAuthors.getLength(); j++) {
                    Element articleAuthor = (Element) articleAuthors.item(j);
                    String authorName = articleAuthor.getAttribute("NOME-PARA-CITACAO");
                    authors.add(authorName);
                }

                Article article = new Article(articleTitle, publisher, volume, pages, articleYear, authors, researcher);
                List<Article> duplicateArticles = articleRepository.findByTitleIgnoreCase(articleTitle);
                if (!duplicateArticles.isEmpty()) {
                    for (Article duplicatedArticle : duplicateArticles) {
                        Researcher authorOne = duplicatedArticle.getResearcher();
                        Researcher authorTwo = researcher;
                        UUID authorOneID = duplicatedArticle.getResearcher().getId();
                        UUID authorTwoID = researcher.getId();
                        String title = duplicatedArticle.getTitle();
                        Collaboration collaboration;
                        switch (authorOneID.compareTo(authorTwoID)) {
                            case -1:
                                collaboration = new Collaboration(authorOne, authorTwo, title, ProductionType.ARTICLE);
                                break;
                            case 1:
                                collaboration = new Collaboration(authorTwo, authorOne, title, ProductionType.ARTICLE);
                                break;
                            case 0:
                                collaboration = new Collaboration(authorOne, authorOne, title, ProductionType.ARTICLE);
                                break;
                            default:
                                return;
                        }
                        collaborationRepository.save(collaboration);
                    }
                }


                articleRepository.save(article);
            }
        }

    }

    private void getResearcherBooks(Researcher researcher, Document doc) {
        NodeList livrosLIsta = doc.getElementsByTagName("LIVRO-PUBLICADO-OU-ORGANIZADO");

        for (int i = 0; i < livrosLIsta.getLength(); i++) {
            Element livroPublicadoElement = (Element) livrosLIsta.item(i);
            Element bookData = (Element) livroPublicadoElement.getElementsByTagName("DADOS-BASICOS-DO-LIVRO").item(0);
            String bookTitle = bookData.getAttribute("TITULO-DO-LIVRO");
            String bookYear = bookData.getAttribute("ANO");

            Element bookDetails = (Element) livroPublicadoElement.getElementsByTagName("DETALHAMENTO-DO-LIVRO").item(0);
            String publisher = bookDetails.getAttribute("NOME-DA-EDITORA");
            String volume = bookDetails.getAttribute("NUMERO-DE-VOLUMES");
            String pages = bookDetails.getAttribute("NUMERO-DE-PAGINAS");

            NodeList bookAuthors = livroPublicadoElement.getElementsByTagName("AUTORES");
            ArrayList<String> authors = new ArrayList<>();
            for (int j = 0; j < bookAuthors.getLength(); j++) {
                Element bookAuthor = (Element) bookAuthors.item(j);
                String authorName = bookAuthor.getAttribute("NOME-PARA-CITACAO");
                authors.add(authorName);
            }
            Book book = new Book(bookTitle, publisher, volume, pages, bookYear, authors, researcher);
            List<Book> duplicateBooks = bookRepository.findByTitleIgnoreCase(bookTitle);
            if (!duplicateBooks.isEmpty()) {
                for (Book duplicateBook : duplicateBooks) {
                    Researcher authorOne = duplicateBook.getResearcher();
                    Researcher authorTwo = researcher;
                    UUID authorOneID = duplicateBook.getResearcher().getId();
                    UUID authorTwoID = researcher.getId();
                    String title = duplicateBook.getTitle();
                    Collaboration collaboration;
                    switch (authorOneID.compareTo(authorTwoID)){
                        case -1:
                            collaboration = new Collaboration(authorOne, authorTwo, title, ProductionType.BOOK);
                            break;
                        case 1:
                            collaboration = new Collaboration(authorTwo, authorOne, title, ProductionType.BOOK);
                            break;
                       /* case 0:
                            collaboration = new Collaboration(authorOne, authorOne, title, ProductionType.BOOK);
                            break;*/
                        default:
                            return;
                    }
                    collaborationRepository.save(collaboration);
                }

            }

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

    @Override
    public Long getResearcherTotalCount() {
        return researcherRepository.count();
    }

    public Page<Researcher> AdvancedSearch(String name, String instituteAcronym, Pageable pageable){
        return researcherRepository.AdvancedSearch(name, instituteAcronym, pageable);
    }

}

