package org.app.fsu_bank_project.exceptions;

public class IllegalCommentException extends RuntimeException{
    public IllegalCommentException(){};
    public IllegalCommentException(String comment){
        super(comment);
    }
}
