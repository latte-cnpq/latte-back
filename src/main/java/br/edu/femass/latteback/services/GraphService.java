package br.edu.femass.latteback.services;

import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.models.graph.Collaboration;
import br.edu.femass.latteback.models.graph.dto.EdgeDTO;
import br.edu.femass.latteback.models.graph.dto.GraphDataDTO;
import br.edu.femass.latteback.models.graph.dto.NodeDTO;
import br.edu.femass.latteback.repositories.CollaborationRepository;
import br.edu.femass.latteback.repositories.InstituteRepository;
import br.edu.femass.latteback.repositories.ResearcherRepository;
import br.edu.femass.latteback.services.interfaces.GraphServiceInterface;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphService implements GraphServiceInterface {

    private final CollaborationRepository collaborationRepository;
    private final InstituteRepository instituteRepository;
    private final ResearcherRepository researcherRepository;


    public GraphService(CollaborationRepository collaborationRepository, InstituteRepository instituteRepository, ResearcherRepository researcherRepository) {
        this.collaborationRepository = collaborationRepository;
        this.instituteRepository = instituteRepository;
        this.researcherRepository = researcherRepository;
    }

    public List<Collaboration> getCollaborationByInstituteName(String instituteName){
        return collaborationRepository.findByInstituteName(instituteName);
    }
    //Todo: researcher name => researcher id
    public GraphDataDTO getGraphDataByFilter(String instituteName, String productionType, String researcherName, String nodeType, Boolean getAll){
        List<Collaboration> collaborationList = collaborationRepository.filterCollaborations(instituteName, productionType, researcherName);

        Map<UUID, NodeDTO> nodesMap = new HashMap<>();

        Map<String, EdgeDTO> researcherEdgesMap = new HashMap<>();
        for (Collaboration collab : collaborationList) {
            Researcher firstAuthor = collab.getFirstAuthor();
            Researcher secondAuthor = collab.getSecondAuthor();
            Institute firstInstitute = firstAuthor.getInstitute();
            Institute secondInstitute = secondAuthor.getInstitute();
            NodeDTO newNode = new NodeDTO();
            NodeDTO newNode2 =new NodeDTO();

            if (nodeType.equalsIgnoreCase("researcher")){
                if (nodesMap.containsKey(firstAuthor.getId())) {
                    nodesMap.get(firstAuthor.getId()).addCount();
                }
                else {
                    newNode.setId(firstAuthor.getId());
                    newNode.setLabel(firstAuthor.getName());
                    nodesMap.put(firstAuthor.getId(), newNode);
                }

                if (nodesMap.containsKey(secondAuthor.getId())) {
                    nodesMap.get(secondAuthor.getId()).addCount();
                }
                else {
                    newNode2.setId(secondAuthor.getId());
                    newNode2.setLabel(secondAuthor.getName());
                    nodesMap.put(secondAuthor.getId(), newNode2);
                }
            }
            else if (nodeType.equalsIgnoreCase("institute")){
                    if (nodesMap.containsKey(firstInstitute.getId()))
                        nodesMap.get(firstInstitute.getId()).addCount();
                    else {
                        newNode.setId(firstInstitute.getId());
                        newNode.setLabel(firstInstitute.getAcronym());
                        nodesMap.put(firstInstitute.getId(), newNode);
                    }
                    if (firstInstitute.getId() != secondInstitute.getId()) {
                    if (nodesMap.containsKey(secondInstitute.getId()))
                        nodesMap.get(secondInstitute.getId()).addCount();
                    else {
                        newNode2.setId(secondInstitute.getId());
                        newNode2.setLabel(secondInstitute.getName());
                        nodesMap.put(secondInstitute.getId(), newNode2);
                    }
                }
            }

            String edgeKey = collab.getFirstAuthor().getId().toString() + "-" + collab.getSecondAuthor().getId().toString();

            if (researcherEdgesMap.containsKey(edgeKey)) {
                researcherEdgesMap.get(edgeKey).addRepetitionCount();
            } else {
                EdgeDTO newEdge = new EdgeDTO();
                if (nodeType.equalsIgnoreCase("researcher")) {
                    newEdge.setSource(collab.getFirstAuthor().getId());
                    newEdge.setTarget(collab.getSecondAuthor().getId());
                }
                else if (!firstAuthor.getInstitute().getId().toString().equalsIgnoreCase(secondAuthor.getInstitute().getId().toString())){
                    newEdge.setSource(firstAuthor.getInstitute().getId());
                    newEdge.setTarget(secondAuthor.getInstitute().getId());
                }
                if (newEdge.getSource() != null && newEdge.getTarget() != null) {

                    newEdge.setId(edgeKey);
                    researcherEdgesMap.put(edgeKey, newEdge);
                }
            }
        }
        if (getAll && nodeType.equalsIgnoreCase("researcher") && instituteName.equals("") && researcherName.equals("")){
            List<Researcher> researchers = researcherRepository.findAll();
            for (Researcher researcher: researchers) {
                if (!nodesMap.containsKey(researcher.getId())) {
                    NodeDTO temp = new NodeDTO();
                    temp.setId(researcher.getId());
                    temp.setLabel(researcher.getName());
                    temp.setCount(0);
                    nodesMap.put(researcher.getId(), temp);}
            }
        }
        if (getAll && nodeType.equalsIgnoreCase("institute")&& instituteName.equals("") && researcherName.equals("")){
            List<Institute> institutes = instituteRepository.findAll();
            for (Institute institute: institutes) {
                if (!nodesMap.containsKey(institute.getId())) {
                    NodeDTO temp = new NodeDTO();
                    temp.setId(institute.getId());
                    temp.setLabel(institute.getAcronym());
                    temp.setCount(0);
                    nodesMap.put(institute.getId(), temp);
                }
            }
        }
        List<Map<String, Object>> nodeData = new ArrayList<>();

        for (NodeDTO node : nodesMap.values()) {
            nodeData.add(node.toJson());
        }

        List<Map<String, Object>> edgeData = new ArrayList<>();
        for (EdgeDTO edge : researcherEdgesMap.values()) {
            edgeData.add(edge.toJson());
        }

        GraphDataDTO graphDataDTO = new GraphDataDTO();
        graphDataDTO.setEdges(edgeData);
        graphDataDTO.setNodes(nodeData);
        return graphDataDTO;
    }
}
