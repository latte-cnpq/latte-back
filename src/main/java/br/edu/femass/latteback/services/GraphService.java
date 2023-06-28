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

    public List<Collaboration> getCollaborationByInstituteName(String instituteName) {
        return collaborationRepository.findByInstituteName(instituteName);
    }

    public GraphDataDTO getGraphDataByFilters(String productionType, List<String> researcherNames, String nodeType) {
        Set<Collaboration> collaborationList = new HashSet<>();


        Boolean mostrarTodosVertices = false;
        Boolean mostrarTodasArestas = false;

        if (researcherNames == null || researcherNames.isEmpty()) {
            mostrarTodosVertices = true;
        }

        Map<UUID, NodeDTO> nodesMap = new HashMap<>();
        Map<String, EdgeDTO> edgesMap = new HashMap<>();

        if (mostrarTodosVertices) {
            if (nodeType.equalsIgnoreCase("researcher")) {
                for (Researcher researcher : researcherRepository.findAll()) {
                        NodeDTO newNode = new NodeDTO();
                        newNode.setLabel(researcher.getName());
                        newNode.setId(researcher.getId());
                        nodesMap.put(researcher.getId(), newNode);
                }
            } else {
                for (Institute institute : instituteRepository.findAll()) {
                        NodeDTO newNode = new NodeDTO();
                        newNode.setId(institute.getId());
                        newNode.setLabel(institute.getAcronym());
                        nodesMap.put(institute.getId(), newNode);
                }
            }
        } else
            if (researcherNames != null && !researcherNames.isEmpty()) {
                for(String researcherName : researcherNames) {
                    for (Researcher researcher : researcherRepository.findByNameContainsIgnoreCase(researcherName)) {
                        if (nodeType.equalsIgnoreCase("researcher")) {
                            if (!nodesMap.containsKey(researcher.getId())) {
                                NodeDTO newNode = new NodeDTO();
                                newNode.setLabel(researcher.getName());
                                newNode.setId(researcher.getId());
                                nodesMap.put(researcher.getId(), newNode);
                            }
                        } else {
                            if (!nodesMap.containsKey(researcher.getInstitute().getId())) {
                                NodeDTO newNode = new NodeDTO();
                                newNode.setId(researcher.getInstitute().getId());
                                newNode.setLabel(researcher.getInstitute().getAcronym());
                                nodesMap.put(researcher.getInstitute().getId(), newNode);
                            }
                        }
                    }
                }

            }

        if (mostrarTodasArestas) {
            if (researcherNames == null || researcherNames.isEmpty()) {
                collaborationList.addAll(collaborationRepository.findAll());
            }else {
                for (String researcherName : researcherNames) {
                    collaborationList.addAll(collaborationRepository.filterCollaborations(productionType, researcherName));
                }
            }
            for (Collaboration collab : collaborationList) {
                if (nodeType.equalsIgnoreCase("researcher")) {
                    if (!nodesMap.containsKey(collab.getFirstAuthor().getId())) {
                        NodeDTO newNode = new NodeDTO();
                        newNode.setLabel(collab.getFirstAuthor().getName());
                        newNode.setId(collab.getFirstAuthor().getId());
                        nodesMap.put(collab.getFirstAuthor().getId(), newNode);

                    }
                    if (!nodesMap.containsKey(collab.getSecondAuthor().getId())) {
                        NodeDTO newNode2 = new NodeDTO();
                        newNode2.setLabel(collab.getSecondAuthor().getName());
                        newNode2.setId(collab.getSecondAuthor().getId());
                        nodesMap.put(collab.getSecondAuthor().getId(), newNode2);
                    }

                } else {
                    if (!nodesMap.containsKey(collab.getFirstAuthor().getInstitute().getId())) {
                        NodeDTO newNode = new NodeDTO();
                        newNode.setId(collab.getFirstAuthor().getInstitute().getId());
                        newNode.setLabel(collab.getFirstAuthor().getInstitute().getAcronym());
                        nodesMap.put(collab.getFirstAuthor().getInstitute().getId(), newNode);
                    }
                    if (!nodesMap.containsKey(collab.getSecondAuthor().getInstitute().getId())) {
                        NodeDTO newNode2 = new NodeDTO();
                        newNode2.setId(collab.getSecondAuthor().getInstitute().getId());
                        newNode2.setLabel(collab.getSecondAuthor().getInstitute().getAcronym());
                        nodesMap.put(collab.getSecondAuthor().getInstitute().getId(), newNode2);
                    }
                }
            }
        }

       if (mostrarTodasArestas) {
            for (Collaboration collab : collaborationList) {
                EdgeDTO newEdge = new EdgeDTO();
                UUID smallerId;
                UUID biggerId;
                if (nodeType.equalsIgnoreCase("researcher")) {
                    if (collab.getFirstAuthor().getId().compareTo(collab.getSecondAuthor().getId()) > 0) {
                        biggerId = collab.getFirstAuthor().getId();
                        smallerId = collab.getSecondAuthor().getId();
                    } else {
                        smallerId = collab.getFirstAuthor().getId();
                        biggerId = collab.getSecondAuthor().getId();
                    }
                    newEdge.setSource(collab.getFirstAuthor().getId());
                    newEdge.setTarget(collab.getSecondAuthor().getId());

                    nodesMap.get(collab.getFirstAuthor().getId()).addCount();
                    nodesMap.get(collab.getSecondAuthor().getId()).addCount();
                } else {
                    Institute firstInstitute = collab.getFirstAuthor().getInstitute();
                    Institute secondInstitute = collab.getSecondAuthor().getInstitute();
                    if (firstInstitute.getId().compareTo(secondInstitute.getId()) > 0) {
                        biggerId = firstInstitute.getId();
                        smallerId = secondInstitute.getId();
                    } else {
                        smallerId = firstInstitute.getId();
                        biggerId = secondInstitute.getId();
                    }
                    if (!firstInstitute.getId().toString().equalsIgnoreCase(secondInstitute.getId().toString())) {
                        newEdge.setSource(smallerId);
                        newEdge.setTarget(biggerId);
                        nodesMap.get(firstInstitute.getId()).addCount();
                        nodesMap.get(secondInstitute.getId()).addCount();
                    }
                }
                if (!smallerId.toString().equalsIgnoreCase(biggerId.toString())) {

                    String edgeKey = smallerId + "-" + biggerId;

                    if (!edgesMap.containsKey(edgeKey)) {
                        newEdge.setId(edgeKey);
                        newEdge.addRepetitionCount();
                        edgesMap.put(edgeKey, newEdge);
                    } else {
                        edgesMap.get(edgeKey).addRepetitionCount();
                    }
                }
            }
        } else
        {
            if (researcherNames != null && !researcherNames.isEmpty()) {
                for (String researcherName : researcherNames) {
                    collaborationList.addAll(collaborationRepository.filterCollaborations(productionType, researcherName));
                }
            } else {
                collaborationList.addAll(collaborationRepository.findByProductionType(productionType));
            }
            //Mostra apenas colaborações entre pesq escolhidos
            for (Collaboration collab : collaborationList) {
                if (
                        (nodesMap.containsKey(collab.getFirstAuthor().getId()) &&
                        nodesMap.containsKey(collab.getSecondAuthor().getId())) ||
                        (nodesMap.containsKey(collab.getFirstAuthor().getInstitute().getId()) &&
                         nodesMap.containsKey(collab.getSecondAuthor().getInstitute().getId()))
                ) {
                    EdgeDTO newEdge = new EdgeDTO();
                    UUID smallerId;
                    UUID biggerId;
                    if (nodeType.equalsIgnoreCase("researcher")) {
                        if (collab.getFirstAuthor().getId().compareTo(collab.getSecondAuthor().getId()) > 0) {
                            biggerId = collab.getFirstAuthor().getId();
                            smallerId = collab.getSecondAuthor().getId();
                        } else {
                            smallerId = collab.getFirstAuthor().getId();
                            biggerId = collab.getSecondAuthor().getId();
                        }
                        newEdge.setSource(collab.getFirstAuthor().getId());
                        newEdge.setTarget(collab.getSecondAuthor().getId());
                        nodesMap.get(collab.getFirstAuthor().getId()).addCount();
                        nodesMap.get(collab.getSecondAuthor().getId()).addCount();
                    } else {
                        Institute firstInstitute = collab.getFirstAuthor().getInstitute();
                        Institute secondInstitute = collab.getSecondAuthor().getInstitute();
                        if (firstInstitute.getId().compareTo(secondInstitute.getId()) > 0) {
                            biggerId = firstInstitute.getId();
                            smallerId = secondInstitute.getId();
                        } else {
                            smallerId = firstInstitute.getId();
                            biggerId = secondInstitute.getId();
                        }
                        if (!firstInstitute.getId().toString().equalsIgnoreCase(secondInstitute.getId().toString())) {
                            newEdge.setSource(firstInstitute.getId());
                            newEdge.setTarget(secondInstitute.getId());
                            nodesMap.get(firstInstitute.getId()).addCount();
                            nodesMap.get(secondInstitute.getId()).addCount();
                        }
                    }
                    if (!smallerId.toString().equalsIgnoreCase(biggerId.toString())) {
                        String edgeKey = smallerId.toString() + "-" + biggerId.toString();
                        if (!edgesMap.containsKey(edgeKey)) {
                            newEdge.setId(edgeKey);
                            newEdge.addRepetitionCount();
                            edgesMap.put(edgeKey, newEdge);

                        } else
                            edgesMap.get(edgeKey).addRepetitionCount();
                    }

                }
            }
        }

        List<Map<String, Object>> nodeData = new ArrayList<>();

        for (NodeDTO node : nodesMap.values()) {
            nodeData.add(node.toJson());
        }

        List<Map<String, Object>> edgeData = new ArrayList<>();
        for (EdgeDTO edge : edgesMap.values()) {
            edgeData.add(edge.toJson());
        }

        GraphDataDTO graphDataDTO = new GraphDataDTO();
        graphDataDTO.setEdges(edgeData);
        if (nodeData.isEmpty()) {
            nodeData.add(new NodeDTO(new UUID(0, 0), "Vazio").toJson());
        }
        graphDataDTO.setNodes(nodeData);

        return graphDataDTO;
    }
}

