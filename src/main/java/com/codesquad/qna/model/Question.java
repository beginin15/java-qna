package com.codesquad.qna.model;

import com.codesquad.qna.model.base.BaseEntity;
import com.codesquad.qna.util.HtmlDocumentUtils;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
public class Question extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User writer;

    @OneToMany(mappedBy = "question")
    private List<Answer> answers;

    @Column(nullable = false)
    @NotEmpty
    private String title;

    @Lob
    private String contents;

    @Formula("(select count(*) from answer a where a.question_id = id and a.deleted = false)")
    private int countOfAnswers;

    @Column(nullable = false)
    private boolean deleted;

    public Question() {}

    public Question(User writer, String title, String contents) {
        this.writer = writer;
        this.title = title;
        this.contents = contents;
    }

    public Long getId() {
        return id;
    }

    public String getWriter() {
        return writer.getUserId();
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public String getContentsWithBr() {
        return HtmlDocumentUtils.getEntertoBrTag(contents);
    }

    public int getCountOfAnswers() {
        return countOfAnswers;
    }

    public boolean matchWriter(User sessionedUser) {
        return writer.equals(sessionedUser);
    }

    public void update(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public boolean delete() {
        if (countOfAnswers == 0)
            return (this.deleted = true);

        for (Answer answer : answers) {
            if (!answer.checkDeleteCondition(writer))
                return false;
        }
        return (this.deleted = true);
    }

    public void addAnswer() {
        countOfAnswers++;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", writer='" + writer + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", " + super.toString() +
                '}';
    }
}
