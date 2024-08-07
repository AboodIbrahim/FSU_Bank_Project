package org.app.fsu_bank_project.data;


import java.io.*;


public class FSUGenBank extends Fasta{

    private String id;
    private Fasta fasta;
    private String accession;
    private String version;
    private String source;

    public FSUGenBank(){
        fasta=new Fasta();
        id ="";
        accession =""; version ="";
        source ="";
    }
    public FSUGenBank(Fasta f,String id, String acc,
                      String ver, String source){
        this.id =id;
        fasta.header =f.getHeader();
        accession ="accession = "+acc;
        version ="version = "+ver;
        this.source ="source = "+source;
        fasta.sequence =f.getSequence();
    }

    public String toString(){
        return  id +"\n"
                + fasta.header +"\n"
                + accession +"\n"
                + version +"\n"
                + source +"\n"
                + fasta.sequence+"\n";
    }

    public Fasta getFasta(){
        return this.fasta;
    }

    public void writeToFile(File file){
        try {
            FileWriter writer=new FileWriter(file);
            writer.write(this.toString());
            writer.flush();
            writer.close();
        }catch (IOException e){
            System.out.println("Datei ist ungültig");
        }
    }

    public void readFromFile(File file) {
        try {
            BufferedReader reader= new BufferedReader(new FileReader(file));
            id =reader.readLine();
            fasta.header =reader.readLine();
            accession =reader.readLine();
            version =reader.readLine();
            source =reader.readLine();
            String c;
            while((c=reader.readLine())!=null){
                fasta.sequence+= c;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void EMBLToFSUGenBank(File file, File file2){
        try {
            BufferedWriter bw= new BufferedWriter(new FileWriter(file2));
            BufferedReader br= new BufferedReader(new FileReader(file));
            String ln;
            String[] arrayln=new String[2];
            while((ln=br.readLine())!=null){
                if(ln.charAt(0)=='I'&&ln.charAt(1)=='D') {arrayln=ln.split("ID");
                    this.id="id:    "+arrayln[1];}
                if(ln.charAt(0)=='D'&&ln.charAt(1)=='E') {arrayln=ln.split("DE");
                    this.fasta.header="header:  >"+arrayln[1];}
                if(ln.charAt(0)=='A'&&ln.charAt(1)=='C') {arrayln=ln.split("AC");
                    this.accession="accession: "+arrayln[1];}
                if(ln.charAt(0)=='S'&&ln.charAt(1)=='V') {arrayln=ln.split("SV");
                    this.version="version:   "+arrayln[1];}
                if(ln.charAt(0)=='O'&&ln.charAt(1)=='S') {arrayln=ln.split("OS");
                    this.source="source:    "+arrayln[1];}
                if(ln.charAt(0)=='S'&&ln.charAt(1)=='Q') {this.fasta.sequence ="sequence: "+"\n";
                    while((ln=br.readLine())!=null)
                        if (ln=="//") this.fasta.sequence += sequence;
                        else {
                            for (int i = 0; i < ln.length(); i++) {
                                if (ln.charAt(i)=='a'||ln.charAt(i)=='t'
                                        ||ln.charAt(i)=='g'||ln.charAt(i)=='c')
                                    this.fasta.sequence += sequence +ln.charAt(i);
                            }
                            this.fasta.sequence += sequence +"\n";
                        }
                }
            }
            br.close();
            bw.write(this.toString());
            bw.flush();
            bw.close();
        } catch (IOException e){
            System.out.println("ungültige Datei!");
        }

    }

    public void GenBankToFSUGenBank(File file, File file2){
        try {
            BufferedWriter bw= new BufferedWriter(new FileWriter(file2));
            BufferedReader br= new BufferedReader(new FileReader(file));
            String ln;
            String[] arrayln= new String[2];
            while((ln=br.readLine())!=null){
                if(ln.charAt(0)=='L'&&ln.charAt(1)=='O') {arrayln=ln.split("LOCUS");
                    this.id="id:       "+arrayln[1];}
                if(ln.charAt(0)=='D'&&ln.charAt(1)=='E') {arrayln=ln.split("DEFINITION");
                    this.fasta.header="header:      >"+arrayln[1];}
                if(ln.charAt(0)=='A'&&ln.charAt(1)=='C') {arrayln=ln.split("ACCESSION");
                    this.accession="accession:    "+arrayln[1];}
                if(ln.charAt(0)=='V'&&ln.charAt(1)=='E') {arrayln=ln.split("VERSION");
                    this.version="version:    "+arrayln[1];}
                if(ln.charAt(0)=='S'&&ln.charAt(1)=='O') {arrayln=ln.split("SOURCE");
                    this.source="source:    "+arrayln[1];}
                if(ln.charAt(0)=='O'&&ln.charAt(1)=='R') {this.fasta.sequence ="sequence:"+"\n";
                    while((ln=br.readLine())!=null)
                        if (ln=="//") this.fasta.sequence += sequence;
                        else {
                            for (int i = 0; i < ln.length(); i++) {
                                if (ln.charAt(i)=='a'||ln.charAt(i)=='t'
                                        ||ln.charAt(i)=='g'||ln.charAt(i)=='c')
                                    this.fasta.sequence += sequence +ln.charAt(i);
                            }
                            this.fasta.sequence += sequence +"\n";
                        }
                }
            }
            br.close();
            bw.write(this.toString());
            bw.flush();
            bw.close();
        } catch (IOException e){
            System.out.println("ungültige Datei!");
            e.printStackTrace();
        }

    }


}
