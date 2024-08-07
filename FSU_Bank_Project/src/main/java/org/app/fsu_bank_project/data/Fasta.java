package org.app.fsu_bank_project.data;

import org.app.fsu_bank_project.exceptions.IllegalCommentException;
import org.app.fsu_bank_project.exceptions.IllegalHeaderException;
import org.app.fsu_bank_project.exceptions.IllegalSequenceException;

import java.io.*;

public class Fasta {
    protected String header;
    protected String sequence;
    protected String comments;


    private void testHeader(){
        if(this.header.charAt(0)!='>'){
            throw new IllegalHeaderException("ungültiger Header!");
        }
    }

    private void testSequence(){
        for (int i = 0; i <this.sequence.length() ; i++) {
            if(this.sequence.charAt(i)!='T'||this.sequence.charAt(i)!='A'||
                    this.sequence.charAt(i)!= 'G'||this.sequence.charAt(i)!='C'){
                throw new IllegalSequenceException("ungültige Sequenz!");
            }
        }
    }

    private void testComment(){
        if(this.comments.charAt(0)!=';'){
            throw new IllegalCommentException("ungültiger Komment!");
        }
    }
    public Fasta(){
        this.header=">";
        this.sequence="";
        this.comments=";";
    }

    public Fasta(String head, String seq){
        header=">"+head;
        sequence=seq;

    }
    public Fasta(String head, String seq, String comm) {
        header=">"+head;
        sequence=seq;
        comments=";"+comm;
    }

    public Fasta(Fasta fasta){
        header=fasta.header;
        sequence=fasta.sequence;
        comments= fasta.comments;;
    }

    public String getHeader(){
        return "header = "+this.header;
    }

    public String getSequence(){
        return this.sequence
                +this.sequence.length();
    }

    public String getComments(){
        return "comment = "+this.comments;
    }


    public String dotPlot(Fasta f1){
        StringBuilder dotPlot;
        String seq1=this.sequence.replace("sequence:","");
        String seq2=f1.sequence.replace("sequence:","");
        dotPlot = new StringBuilder("  " + seq1+ "\n");
        for (int i = 0; i < seq1.length(); i++) {
            dotPlot.append(seq2.charAt(i)).append(" ");
            for (int j = 0; j < seq2.length(); j++) {
                if(seq1.charAt(j)==seq2.charAt(i)){
                    dotPlot.append("*");
                } else dotPlot.append(" ");
            }
            dotPlot.append("\n");
        }
        dotPlot.append("finished");
        return dotPlot.toString();
    }


    public void writeToFile(){
        try {
            File file= new File("src/main/resources/Files/Server/TempTransformer.txt");
            FileWriter writer=new FileWriter(file, true);
            writer.write(this.header+"\n");
            for (int i = 0; i <this.sequence.length() ; i++) {
                if(i%80==0) writer.write(this.sequence.charAt(i)+"\n");
                else writer.write(this.sequence.charAt(i));
            }
            writer.write(this.comments+"\n");
            writer.flush();
        }catch (IOException e){
            System.out.println("Datei ist ungültig");
        }
    }

    public void readFromFile(File file) {
        try {
            BufferedReader reader=
                    new BufferedReader(new FileReader(file));
            this.header=reader.readLine();
            this.testHeader();
            this.sequence=reader.readLine();
            this.testSequence();
            this.comments=reader.readLine();
            this.testComment();
        } catch (IOException e) {
            System.out.println("leere Datei");
        }
    }




}
