package br.edu.femass.latteback.services;

import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.models.graph.Collaboration;
import br.edu.femass.latteback.models.graph.dto.EdgeDTO;
import br.edu.femass.latteback.models.graph.dto.GraphDataDTO;
import br.edu.femass.latteback.models.graph.dto.NodeDTO;
import br.edu.femass.latteback.repositories.CollaborationRepository;
import br.edu.femass.latteback.services.interfaces.GraphServiceInterface;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphService implements GraphServiceInterface {

    private final CollaborationRepository collaborationRepository;

    public GraphService(CollaborationRepository collaborationRepository) {
        this.collaborationRepository = collaborationRepository;
    }

    public List<Collaboration> getCollaborationByInstituteName(String instituteName){
        return collaborationRepository.findByInstituteName(instituteName);
    }
    //Todo: researcher name => researcher id
    public GraphDataDTO getGraphDataByFilter(String instituteName, String productionType, String researcherName, String nodeType){
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
                else {
                    newEdge.setSource(collab.getFirstAuthor().getInstitute().getId());
                    newEdge.setTarget(collab.getSecondAuthor().getInstitute().getId());
                }
                newEdge.setId(edgeKey);
                researcherEdgesMap.put(edgeKey, newEdge);
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
        /*for (EdgeDTO edge : researcherEdgesMap.values().stream().toList()){
            EdgeDTO newEdge = new EdgeDTO();
            newEdge.setSourceId(edge.getSourceId());
            newEdge.setTargetId(edge.getTargetId());
            newEdge.setRepetitionCount(edge.getRepetitionCount());
            edgeData.add(newEdge);
        }*/

        GraphDataDTO graphDataDTO = new GraphDataDTO();
        graphDataDTO.setEdges(edgeData);
        graphDataDTO.setNodes(nodeData);
        return graphDataDTO;
    }
}
