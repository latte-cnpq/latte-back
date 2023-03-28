package br.edu.femass.latteback.services;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import br.edu.femass.latteback.dto.ResearcherDto;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.models.ResearcherCache;
import br.edu.femass.latteback.repositories.ResearcherCacheRepository;
import br.edu.femass.latteback.repositories.ResearcherRepository;
import br.edu.femass.latteback.services.interfaces.RResearcherService;
import br.edu.femass.latteback.utils.enums.ResearcherField;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import jakarta.transaction.Transactional;

@Service
public class ResearcherService implements RResearcherService {//Todo:Remover comentários se necessário
    private final ResearcherRepository _researcherRepository;
    private final ResearcherCacheRepository _researcherCacheRepository;
    private static final ArrayList<String> filesOnCache = new ArrayList<>();

    public ResearcherService(ResearcherRepository researcherRepository, ResearcherCacheRepository researcherCacheRepository) {

        this._researcherRepository = researcherRepository;
        this._researcherCacheRepository = researcherCacheRepository;
        var researchersOnCache = _researcherCacheRepository.findAll();
        for (ResearcherCache rc : researchersOnCache){
            filesOnCache.add(rc.getFileName());
        }
    }

    private final static String resumePath = "src/main/resources/resume/";
    
    @Override
    @Transactional
    public Researcher save(String researcheridNumber) {
        if(researcheridNumber.isBlank()) {
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

        var researcher = new Researcher();
        researcher.setName(researcheridNumber);
        researcher.setResearcheridNumber(researcheridNumber);
        BeanUtils.copyProperties(researcheridNumber, researcherFile);
        
        return (_researcherRepository.save(getResearcherFromFile(researcherFile)));
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

                    Document doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();

                    Node curriculoVitae = doc.getElementsByTagName("CURRICULO-VITAE").item(0);
                    Element curriculoVitaeElement = (Element) curriculoVitae;
                    String numeroIdentificador = curriculoVitaeElement.getAttribute("NUMERO-IDENTIFICADOR");
                    
                    Node dadosGerais = doc.getElementsByTagName("DADOS-GERAIS").item(0);
                    Element dadosGeraisElement = (Element) dadosGerais;
                    String nomeCompleto = dadosGeraisElement.getAttribute("NOME-COMPLETO");


                    //Salva pesquisador em cache
                    ResearcherCache researcherCache = new ResearcherCache(nomeCompleto,numeroIdentificador, file.getName());
                    _researcherCacheRepository.save(researcherCache);
                    filesOnCache.add(file.getName());

                    if (researcherNumber.equals(numeroIdentificador))
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

            Node curriculoVitae = doc.getElementsByTagName("CURRICULO-VITAE").item(0);
            Element curriculoVitaeElement = (Element) curriculoVitae;
            String numeroIdentificador = curriculoVitaeElement.getAttribute("NUMERO-IDENTIFICADOR");
            
            Node dadosGerais = doc.getElementsByTagName("DADOS-GERAIS").item(0);
            Element dadosGeraisElement = (Element) dadosGerais;
            String nomeCompleto = dadosGeraisElement.getAttribute("NOME-COMPLETO");
            
            Node resumoCV = doc.getElementsByTagName("RESUMO-CV").item(0);
            Element resumoCVElement = (Element) resumoCV;
            String resume = resumoCVElement.getAttribute("TEXTO-RESUMO-CV-RH");

            //Todo: Ler restante do XML


            //Salva pesquisador 
             researcher = new Researcher(nomeCompleto, numeroIdentificador, resume);
            return researcher;          
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new IllegalArgumentException ("File not found.");
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

        if(!foundResearcher.isPresent()) {
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

