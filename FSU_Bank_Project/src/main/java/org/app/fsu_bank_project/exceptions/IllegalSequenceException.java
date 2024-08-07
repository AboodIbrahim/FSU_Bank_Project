package org.app.fsu_bank_project.exceptions;

public class IllegalSequenceException extends RuntimeException{
    public IllegalSequenceException(){};

    public IllegalSequenceException(String sequence){
        super(sequence);
    }

}
