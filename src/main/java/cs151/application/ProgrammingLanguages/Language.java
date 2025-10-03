package cs151.application.ProgrammingLanguages;

public class Language {
    private String languageName;

    public Language(String languageName){
        if(languageName == null || languageName.trim().isEmpty()){
            throw new IllegalArgumentException("Invalid argument. Language Object could not be created");
        }
        this.languageName = languageName;
    }

    public String getLanguageName(){
        return languageName;
    }

}
