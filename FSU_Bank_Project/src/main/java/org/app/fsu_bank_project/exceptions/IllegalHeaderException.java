package org.app.fsu_bank_project.exceptions;

public class IllegalHeaderException extends RuntimeException{

    public IllegalHeaderException(){};

    public IllegalHeaderException(String header){
        super(header);
    }



}
