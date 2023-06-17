package br.edu.femass.latteback.services;

import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.models.graph.Collaboration;
import br.edu.femass.latteback.models.graph.dto.EdgeDTO;
import br.edu.femass.latteback.models.graph.dto.GraphDataDTO;
import br.edu.femass.latteback.models.graph.dto.NodeDTO;
import br.edu.femass.latteback.models.repositories.CollaborationRepository;
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
    public GraphDataDTO getGraphDataByFilter(String instituteName, String productionType, String researcherName){
        List<Collaboration> collaborationList = collaborationRepository.filterCollaborations(instituteName, productionType, researcherName);

        Set<Researcher> uniqueResearchers = new HashSet<>();
        Map<String, EdgeDTO> researcherEdgesMap = new HashMap<>();
        for (Collaboration collab : collaborationList) {
            uniqueResearchers.add(collab.getFirstAuthor());
            uniqueResearchers.add(collab.getSecondAuthor());

            String edgeKey = collab.getFirstAuthor().getId().toString() + "-" + collab.getSecondAuthor().getId().toString();

            if (researcherEdgesMap.containsKey(edgeKey)) {
                researcherEdgesMap.get(edgeKey).addRepetitionCount();
            } else {
                EdgeDTO newEdge = new EdgeDTO();
                newEdge.setSource(collab.getFirstAuthor().getId());
                newEdge.setTarget(collab.getSecondAuthor().getId());
                newEdge.setId(edgeKey);
                researcherEdgesMap.put(edgeKey, newEdge);
            }
        }

        List<Map<String, Object>> nodeData = new ArrayList<>();

        for (Researcher researcher : uniqueResearchers) {
            NodeDTO node = new NodeDTO();
            node.setId(researcher.getId());
            node.setResearcherLabel(researcher.getName());
            node.setInstituteLabel(researcher.getInstitute().getAcronym());
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
