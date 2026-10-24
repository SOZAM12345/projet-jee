package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.Document;
import ma.fsbm.lias_lab.entity.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByArchiveFalseOrderByDateUploadDesc();

    List<Document> findByArchiveTrueOrderByDateUploadDesc();

    List<Document> findByEvenementIdOrderByDateUploadDesc(Long evenementId);

    List<Document> findByTypeOrderByDateUploadDesc(TypeDocument type);

    // Section 10 : "Recherche"
    List<Document> findByTitreContainingIgnoreCaseOrderByDateUploadDesc(String motCle);
}