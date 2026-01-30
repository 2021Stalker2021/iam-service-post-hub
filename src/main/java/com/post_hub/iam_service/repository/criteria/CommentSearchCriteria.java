package com.post_hub.iam_service.repository.criteria;

import com.post_hub.iam_service.model.entity.Comment;
import com.post_hub.iam_service.model.request.comment.CommentSearchRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class CommentSearchCriteria implements Specification<Comment> {
    private final CommentSearchRequest request;

    @Override
    public Predicate toPredicate(
            @NonNull Root<Comment> root,
            CriteriaQuery<?> query,
            @NonNull CriteriaBuilder criteriaBuilder) {

        /*
        Root<Post> - корневая сущность запроса (посты).
        CriteriaQuery<?> - объект для построения JPQL-запроса.
        CriteriaBuilder - фабрика для создания условий (predicates).
        ___________________________________________________________
        Ищет посты, где заголовок содержит указанный текст (регистронезависимо).
        Post.TITLE_NAME_FIELD - константа с именем поля в БД
         */

        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(request.getMessage())) {
            predicates.add(criteriaBuilder.like(root.get(Comment.MESSAGE_NAME_FIELD), "%" + request.getMessage() + "%"));
        }

        if (Objects.nonNull(request.getCreatedBy())) {
            predicates.add(criteriaBuilder.like(root.get(Comment.CREATED_BY_FIELD), "%" + request.getCreatedBy() + "%"));
        }

        if (Objects.nonNull(request.getDeleted())) {
            predicates.add(criteriaBuilder.equal(root.get(Comment.DELETED_FIELD), request.getDeleted()));
        }

        if (Objects.nonNull(request.getPostId())) {
            predicates.add(criteriaBuilder.equal(root.get("post").get("id"), request.getPostId()));
        }

        if (Objects.nonNull(request.getKeyword())) {
            Predicate keywordPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get(Comment.MESSAGE_NAME_FIELD), "%" + request.getKeyword() + "%"),
                    criteriaBuilder.like(root.get(Comment.CREATED_BY_FIELD), "%" + request.getKeyword() + "%")
            );
            predicates.add(keywordPredicate);
        }

        /*
        Важный момент! Использует criteriaBuilder.or() для поиска в заголовке ИЛИ содержании.
        Пользователь может искать по любому ключевому слову
         */

        sort(root, criteriaBuilder, query);

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        /*
        Объединяет все условия через AND.
        toArray(new Predicate[0]) - эффективное преобразование списка в массив.
        Создается массив нулевого размера (size = 0).
        Внутри метода toArray() Java сама определяет нужный размер и создает правильный массив.
         */
    }

    private void sort(Root<Comment> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query) {
        if (Objects.nonNull(request.getSortField())) {
            switch (request.getSortField()) {
                case MESSAGE -> query.orderBy(criteriaBuilder.desc(root.get(Comment.MESSAGE_NAME_FIELD)));
                case CREATED_BY -> query.orderBy(criteriaBuilder.desc(root.get(Comment.CREATED_BY_FIELD)));
                default -> query.orderBy(criteriaBuilder.desc(root.get(Comment.ID_FIELD)));
            }
        } else {
            query.orderBy(criteriaBuilder.desc(root.get(Comment.ID_FIELD)));
        }
    }
}
