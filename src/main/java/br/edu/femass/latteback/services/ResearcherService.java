package br.edu.femass.latteback.services;

import br.edu.femass.latteback.dto.ResearcherDto;
import br.edu.femass.latteback.models.Book;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.models.ResearcherCache;
import br.edu.femass.latteback.repositories.BookRepository;
import br.edu.femass.latteback.repositories.ResearcherCacheRepository;
import br.edu.femass.latteback.repositories.ResearcherRepository;
import br.edu.femass.latteback.services.interfaces.RResearcherService;
import br.edu.femass.latteback.utils.enums.ResearcherField;
import jakarta.transaction.Transactional;
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
public class ResearcherService implements RResearcherService {//Todo:Remover comentários se necessário
    private final ResearcherRepository _researcherRepository;
    private final BookRepository _bookRepository;
    private final ResearcherCacheRepository _researcherCacheRepository;
    private static final ArrayList<String> filesOnCache = new ArrayList<>();

    public ResearcherService(ResearcherRepository researcherRepository, ResearcherCacheRepository researcherCacheRepository, BookRepository bookRepository) {

        this._researcherRepository = researcherRepository;
        this._researcherCacheRepository = researcherCacheRepository;
        this._bookRepository = bookRepository;
        var researchersOnCache = _researcherCacheRepository.findAll();
        for (ResearcherCache rc : researchersOnCache){
            filesOnCache.add(rc.getFileName());
        }
    }

    private final static String resumePath = "src/main/resources/resume/";
    
    @Override
    @Transactional
    public Researcher save(String researcheridNumber, UUID instituteId) {
        if( researcheridNumber.isEmpty() || researcheridNumber.isBlank()) {
            throw new IllegalArgumentException("Número do pesquisador não informado.");
        }
        if (_researcherRepository.findFirstByResearcheridNumberContainsIgnoreCase(researcheridNumber).isPresent()){
            throw new IllegalArgumentException("O pesquisador já foi salvo.");
        }

        Optional<ResearcherCache> researcherCache = findResearcherOnCache(researcheridNumber);
        String researcherFile;
        if (researcherCache.isPresent()){
            //saveResearcherFromFile;
            researcherFile = researcherCache.get().getFileName();
        }
        else {
            researcherFile = searchFiles(researcheridNumber).getFileName();
        }

        var researcher = getResearcherFromFile(researcherFile);
        researcher.setInstituteID(instituteId);
        
        return (_researcherRepository.save(researcher));
    }

    public Optional<ResearcherCache> findResearcherOnCache(String researcheridNumber){
        return _researcherCacheRepository.findFirstByResearcheridNumber(researcheridNumber);
    }

    public ResearcherCache searchFiles(String researcherNumber){

        try{
            File folder = new File(resumePath);
            File[] files = folder.listFiles();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            //Atravessa pelos arquivos xml na pasta
            for (File file : files) {
                if (file.isFile() && file.getName().contains(".xml") && !filesOnCache.contains(file.getName())) {
                    String identificationNumber = file.getName().replace(".xml", "").trim();

                    Document doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();

                    Node curriculoVitae = doc.getElementsByTagName("CURRICULO-VITAE").item(0);

                    String nomeCompleto = getString(doc, "DADOS-GERAIS", "NOME-COMPLETO");

                    //Salva pesquisador em cache
                    ResearcherCache researcherCache = new ResearcherCache(nomeCompleto,identificationNumber, file.getName());
                    _researcherCacheRepository.save(researcherCache);
                    filesOnCache.add(file.getName());

                    if (researcherNumber.equals(identificationNumber))
                    {
                        return researcherCache;
                    }
                    
                }
            }   
        }catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Número identificador não encontrado.");
    }
    public Researcher getResearcherFromFile(String filename){
        Researcher researcher;
        try{
            File file = new File(resumePath + "/" + filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            String numeroIdentificador = getString(doc, "CURRICULO-VITAE", "NUMERO-IDENTIFICADOR");
            String nomeCompleto = getString(doc, "DADOS-GERAIS", "NOME-COMPLETO");
            String resume = getString(doc, "RESUMO-CV", "TEXTO-RESUMO-CV-RH");

            //Todo: Ler restante do XML

            //Salva pesquisador 
             researcher = new Researcher(nomeCompleto, numeroIdentificador, resume);

            getResearcherBooks(researcher, doc);

            return researcher;
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new IllegalArgumentException ("File not found.");
    }

    private void getResearcherBooks(Researcher researcher, Document doc) {
        NodeList livrosLIsta = doc.getElementsByTagName("LIVRO-PUBLICADO-OU-ORGANIZADO");

        for (int i = 0; i<livrosLIsta.getLength();i++){
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
            for (int j = 0; j<autoresLivro.getLength();j++){
                Element autorArtigo = (Element) autoresLivro.item(j);
                String nomeAutor = autorArtigo.getAttribute("NOME-PARA-CITACAO");
                autores.add(nomeAutor);
            }
            Book book = new Book(tituloLivro, publisher, volume, paginas, anoLivro, autores, researcher);
            _bookRepository.save(book);
        }
    }

    private static String getString(Document doc, String tagname, String name) {
        Node curriculoVitae = doc.getElementsByTagName(tagname).item(0);
        Element curriculoVitaeElement = (Element) curriculoVitae;
        return curriculoVitaeElement.getAttribute(name);
    }

    public List<Researcher> getAll(){
        return _researcherRepository.findAll();
    }

    public Researcher getById(UUID id){
        if(id == null){
            throw new IllegalArgumentException("ID procurado não pode ser nulo.");
        }

        var researcher = _researcherRepository.findById(id);

        if(researcher.isEmpty()) {
            throw new IllegalArgumentException("Não há pesquisador cadastrado com esse id");
        }

        return  researcher.get();
    }

    public void delete(UUID id){
        var existResearcher = _researcherRepository.existsById(id);

        if(!existResearcher) {
            throw new IllegalArgumentException("Não há pesquisador cadastrado com esse id");
        }

        _researcherRepository.deleteById(id);

    }
 
    public Researcher update(ResearcherDto researcherDto){
        if(researcherDto == null) {
            throw new IllegalArgumentException("Objeto pesquisador nulo");

        }

        var foundResearcher = _researcherRepository.findById(researcherDto.getId());

        if(foundResearcher.isEmpty()) {
            throw new IllegalArgumentException("Pesquisador não encontrado");
        }

        foundResearcher.get().setName(researcherDto.getName());
        foundResearcher.get().setEmail(researcherDto.getEmail());
        foundResearcher.get().setResearcheridNumber(researcherDto.getResearcheridNumber());
        foundResearcher.get().setResume(researcherDto.getResume());

        return _researcherRepository.save(foundResearcher.get());
    }

    public List<Researcher> filterResearcherByTextSearch(String textSearch, ResearcherField field) {
        if(textSearch == null || textSearch.isBlank() || textSearch.isEmpty()) {
           return getAll();
        }

        return switch (field) {
            case NAME -> _researcherRepository.findByNameContainsIgnoreCase(textSearch);
            case RESEARCHERIDNUMBER -> _researcherRepository.findByNameContainsIgnoreCaseOrResearcheridNumber(textSearch, textSearch);
            default -> _researcherRepository.findByNameContainsIgnoreCaseOrResearcheridNumber(textSearch, textSearch);
        };
    }
}

