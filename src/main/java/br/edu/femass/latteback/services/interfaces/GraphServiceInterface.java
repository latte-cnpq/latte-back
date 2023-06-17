package br.edu.femass.latteback.services.interfaces;

import br.edu.femass.latteback.models.graph.Collaboration;

import java.util.List;

public interface GraphServiceInterface {
    List<Collaboration> getCollaborationByInstituteName(String instituteName);


}
