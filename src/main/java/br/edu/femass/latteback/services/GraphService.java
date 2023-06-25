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

    //Todo: researcher name => researcher id
    public GraphDataDTO getGraphDataByFilter(String productionType, List<String> researcherNames, String nodeType) {
        Set<Collaboration> collaborationList = new HashSet<>();

        Boolean mostrarTodosVertices = false;
        Boolean mostrarTodasArestas = false;

        if (researcherNames == null || researcherNames.isEmpty()) {
            mostrarTodosVertices = true;
        }

        Map<UUID, NodeDTO> nodesMap = new HashMap<>();
        Map<String, EdgeDTO> edgesMap = new HashMap<>();
        Set<Researcher> uniqueResearchers = new HashSet<>();

        if (mostrarTodosVertices)
            uniqueResearchers.addAll(researcherRepository.findAll());// mostrar todos os pesquisadores
        if (mostrarTodasArestas)
            collaborationList.addAll(collaborationRepository.findAll());

        if (researcherNames != null) {
            for (String researcher : researcherNames) {
                if (!mostrarTodosVertices)
                    uniqueResearchers.addAll(researcherRepository.findByNameContainsIgnoreCase(researcher)); //mostrar apenas pesquisadores selecionados
                if (!mostrarTodasArestas)
                    collaborationList.addAll(collaborationRepository.filterCollaborations(productionType, researcher));

            }

        }

        Set<Institute> uniqueInstitutes = new HashSet<>();
        if (nodeType.equalsIgnoreCase("institute")) {

            if (mostrarTodosVertices)
                uniqueInstitutes.addAll(instituteRepository.findAll()); //mostrar todos os institutos
            else {
                for (Researcher researcher : uniqueResearchers) {
                    uniqueInstitutes.add(researcher.getInstitute());
                    System.out.println(researcher.getName());//mostrar apenas instituto dos pesquisadores selecionados
                }
            }
        } else for (Researcher researcher : uniqueResearchers)
            System.out.println(researcher.getName());

        for (Collaboration collab : collaborationList) {
            Researcher firstAuthor;
            Researcher secondAuthor;

            if (collab.getFirstAuthor().getId().compareTo(collab.getSecondAuthor().getId()) > 0) {
                secondAuthor = collab.getSecondAuthor();
                firstAuthor = collab.getFirstAuthor();
            } else {
                secondAuthor = collab.getFirstAuthor();
                firstAuthor = collab.getSecondAuthor();
            }

            Institute firstInstitute = firstAuthor.getInstitute();
            Institute secondInstitute = secondAuthor.getInstitute();
            // System.out.println(firstInstitute.getName() + " " + secondInstitute.getName() + " " + collab.getProductionTitle());
            NodeDTO newNode = new NodeDTO();
            NodeDTO newNode2 = new NodeDTO();

            if (nodeType.equalsIgnoreCase("researcher")) {
                if (nodesMap.containsKey(firstAuthor.getId())) {
                    if ((uniqueResearchers.contains(firstAuthor) && uniqueResearchers.contains(secondAuthor)))
                        nodesMap.get(firstAuthor.getId()).addCount();

                } else if (uniqueResearchers.contains(firstAuthor)) {
                    newNode.setId(firstAuthor.getId());
                    newNode.setLabel(firstAuthor.getName());
                    newNode.addCount();
                    ;
                    newNode.setInstituteId(firstInstitute.getId());
                    nodesMap.put(firstAuthor.getId(), newNode);
                } else
                    //System.out.println(firstAuthor.getName() +" " + secondAuthor.getName() + " " + collab.getProductionTitle());

                    if (nodesMap.containsKey(secondAuthor.getId())) {
                        if ((uniqueResearchers.contains(firstAuthor) && uniqueResearchers.contains(secondAuthor)))
                            nodesMap.get(secondAuthor.getId()).addCount();

                    } else if (uniqueResearchers.contains(secondAuthor)) {
                        newNode2.setId(secondAuthor.getId());
                        newNode2.setLabel(secondAuthor.getName());
                        newNode2.addCount();
                        ;
                        newNode2.setInstituteId(secondInstitute.getId());
                        nodesMap.put(secondAuthor.getId(), newNode2);
                    }
                //else
                //System.out.println(firstAuthor.getName() +" " + secondAuthor.getName() + " " + collab.getProductionTitle());
            } else if (nodeType.equalsIgnoreCase("institute")) {
                if (nodesMap.containsKey(firstInstitute.getId())) {
                    if (firstInstitute.getId() != secondInstitute.getId()) {
                        if (uniqueResearchers.contains(firstAuthor) && uniqueResearchers.contains(secondAuthor)) {
                            nodesMap.get(firstInstitute.getId()).addCount();
                        }
                    }

                } else if (uniqueInstitutes.contains(firstInstitute)) {
                    newNode.setId(firstInstitute.getId());
                    newNode.addCount();
                    ;
                    newNode.setLabel(firstInstitute.getAcronym());
                    nodesMap.put(firstInstitute.getId(), newNode);
                }
                if (firstInstitute.getId() != secondInstitute.getId()) {
                    if (nodesMap.containsKey(secondInstitute.getId())) {

                        if (uniqueResearchers.contains(firstAuthor) && uniqueResearchers.contains(secondAuthor)) {
                            nodesMap.get(secondInstitute.getId()).addCount();

                        }
                    } else if (uniqueInstitutes.contains(secondInstitute)) {
                        newNode2.setId(secondInstitute.getId());
                        newNode2.addCount();
                        ;
                        newNode2.setLabel(secondInstitute.getAcronym());
                        nodesMap.put(secondInstitute.getId(), newNode2);
                    }
                }
                // else System.out.println(firstInstitute.getName() + " " + secondInstitute.getName());
            }

            String edgeKey;
            UUID smallerid;
            UUID biggerid;
            if (nodeType.equalsIgnoreCase("researcher")) {
                if (firstAuthor.getId().compareTo(secondAuthor.getId()) > 0) {
                    smallerid = secondAuthor.getId();
                    biggerid = firstAuthor.getId();
                } else {
                    smallerid = firstAuthor.getId();
                    biggerid = secondAuthor.getId();
                }

            } else {
                if (firstInstitute.getId().compareTo(secondInstitute.getId()) > 0) {
                    smallerid = secondInstitute.getId();
                    biggerid = firstInstitute.getId();
                } else {
                    smallerid = firstInstitute.getId();
                    biggerid = secondInstitute.getId();
                }
            }
            edgeKey = smallerid.toString() + "-" + biggerid.toString();

            if (edgesMap.containsKey(edgeKey)) {//Se já tem uma colaboração entre pesquisadores -> aumentar número de ocorrências
                edgesMap.get(edgeKey).addRepetitionCount();
            } else {
                if (uniqueResearchers.contains(firstAuthor) && uniqueResearchers.contains(secondAuthor)) {
                    EdgeDTO newEdge = new EdgeDTO();
                    if (nodeType.equalsIgnoreCase("researcher")) {//Se tipo de vértice for pesquisador
                        newEdge.setSource(firstAuthor.getId());
                        newEdge.setTarget(secondAuthor.getId());
                    }
                    //Se tipo de vértice for instituto e pesquisadores forem de institutos diferentes
                    else if (!firstAuthor.getInstitute().getId().toString().equalsIgnoreCase(secondAuthor.getInstitute().getId().toString())) {
                        newEdge.setSource(firstAuthor.getInstitute().getId());
                        newEdge.setTarget(secondAuthor.getInstitute().getId());
                    }
                    if (newEdge.getSource() != null && newEdge.getTarget() != null) {
                        newEdge.setId(edgeKey);
                        edgesMap.put(edgeKey, newEdge);
                    }
                }
                //else
                // System.out.println(firstAuthor.getName() +" " + secondAuthor.getName() + " " + collab.getProductionTitle());
            }
        }
        //TODO: Teste
        //Selecionar todos os pesquisadores
        if (nodeType.equalsIgnoreCase("researcher") && (researcherNames == null || mostrarTodosVertices)) {
            List<Researcher> researchers = researcherRepository.findAll();
            for (Researcher researcher : researchers) {
                if (!nodesMap.containsKey(researcher.getId()) && uniqueResearchers.contains(researcher)) {//Adicionar todos os pesquisadores que não tenham colaboração
                    NodeDTO temp = new NodeDTO();
                    temp.setId(researcher.getId());
                    temp.setLabel(researcher.getName());
                    temp.setCount(0);
                    temp.setInstituteId(researcher.getInstitute().getId());
                    nodesMap.put(researcher.getId(), temp);
                }
            }
        }
        //Selecionar todos os institutos
        if (nodeType.equalsIgnoreCase("institute") && (researcherNames == null || mostrarTodosVertices)) {
            List<Institute> institutes = instituteRepository.findAll();
            for (Institute institute : institutes) {
                if (!nodesMap.containsKey(institute.getId()) && uniqueInstitutes.contains(institute)) {//Adicionar todos os institutos que não tenham colaboração
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


    public GraphDataDTO getGraphDataByFilters(String productionType, List<String> researcherNames, String nodeType) {
        Set<Collaboration> collaborationList = new HashSet<>();


        Boolean mostrarTodosVertices = false;
        Boolean mostrarTodasArestas = true;

        if (researcherNames == null || researcherNames.isEmpty()) {
            mostrarTodosVertices = true;
        }

        Map<UUID, NodeDTO> nodesMap = new HashMap<>();
        Map<String, EdgeDTO> edgesMap = new HashMap<>();

        if (mostrarTodosVertices) {
            if (nodeType.equalsIgnoreCase("researcher")) {
                for (Researcher researcher : researcherRepository.findAll()) {
                    if (!nodesMap.containsKey(researcher.getId())) {
                        NodeDTO newNode = new NodeDTO();
                        newNode.setLabel(researcher.getName());
                        newNode.setId(researcher.getId());
                        nodesMap.put(researcher.getId(), newNode);
                    }
                }
            } else {
                for (Institute institute : instituteRepository.findAll()) {
                    if (!nodesMap.containsKey(institute.getId())) {
                        NodeDTO newNode = new NodeDTO();
                        newNode.setId(institute.getId());
                        newNode.setLabel(institute.getAcronym());
                        nodesMap.put(institute.getId(), newNode);
                    }
                }
            }
        }else
            if (researcherNames != null && !researcherNames.isEmpty()) {
                for(String researcherName : researcherNames)
                for (Researcher researcher:researcherRepository.findByNameContainsIgnoreCase(researcherName)){
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
                /*if (mostrarTodasArestas) {
                    for (String researcherName : researcherNames) {
                        collaborationList.addAll(collaborationRepository.filterCollaborations(productionType, researcherName));
                    }
                    for (Collaboration collab : collaborationList) {
                        if (nodeType.equalsIgnoreCase("researcher")) {
                            if (!nodesMap.containsKey(collab.getFirstAuthor().getId()) &&

                                        nodesMap.containsKey(collab.getSecondAuthor().getId())) {
                                NodeDTO newNode = new NodeDTO();
                                newNode.setLabel(collab.getFirstAuthor().getName());
                                newNode.setId(collab.getFirstAuthor().getId());
                                nodesMap.put(collab.getFirstAuthor().getId(), newNode);
                            }
                            if (!nodesMap.containsKey(collab.getSecondAuthor().getId())&&
                                    nodesMap.containsKey(collab.getFirstAuthor().getId()) ) {
                                NodeDTO newNode2 = new NodeDTO();
                                newNode2.setLabel(collab.getSecondAuthor().getName());
                                newNode2.setId(collab.getSecondAuthor().getId());
                                nodesMap.put(collab.getSecondAuthor().getId(), newNode2);
                            }
                        } else {
                            if (!nodesMap.containsKey(collab.getFirstAuthor().getInstitute().getId())
                            && (nodesMap.containsKey(collab.getSecondAuthor().getInstitute().getId()))) {
                                NodeDTO newNode = new NodeDTO();
                                newNode.setId(collab.getFirstAuthor().getInstitute().getId());
                                newNode.setLabel(collab.getFirstAuthor().getInstitute().getAcronym());
                                nodesMap.put(collab.getFirstAuthor().getInstitute().getId(), newNode);
                            }
                            if (!nodesMap.containsKey(collab.getSecondAuthor().getInstitute().getId())&&
                                nodesMap.containsKey(collab.getFirstAuthor().getInstitute().getId())) {
                                NodeDTO newNode2 = new NodeDTO();
                                newNode2.setId(collab.getSecondAuthor().getInstitute().getId());
                                newNode2.setLabel(collab.getSecondAuthor().getInstitute().getAcronym());
                                nodesMap.put(collab.getSecondAuthor().getInstitute().getId(), newNode2);
                            }
                        }
                    }
                }*/
            }



        /*if (mostrarTodasArestas) {
            System.out.println(nodesMap);
            for (Collaboration collab : collaborationRepository.findAll()) {
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
                        smallerId = collab.getSecondAuthor().getId();
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
                    } else {
                        edgesMap.get(edgeKey).addRepetitionCount();
                    }
                }
            }
        } else */{
            if (researcherNames != null && !researcherNames.isEmpty()) {
                for (String researcherName : researcherNames) {
                    collaborationList.addAll(collaborationRepository.filterCollaborations(productionType, researcherName));
                }
            } else {
                collaborationList.addAll(collaborationRepository.findAll());
            }
            //Mostra apenas colaborações entre pesq escolhidos
            for (Collaboration collab : collaborationList) {
                if ((nodesMap.containsKey(collab.getFirstAuthor().getId()) &&
                        nodesMap.containsKey(collab.getSecondAuthor().getId())) ||
                        (nodesMap.containsKey(collab.getFirstAuthor().getInstitute().getId()) &&
                                nodesMap.containsKey(collab.getSecondAuthor().getInstitute().getId()))) {
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

