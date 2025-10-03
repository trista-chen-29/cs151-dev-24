package cs151.application.ProgrammingLanguages;

public class PLController {
    // DAO class instance variable would be created here.
    //private final ProgrammingLanguagesDAO repo = new ProgrammingLanguagesDAO();

    public PLController(){
    }


    public String saveLanguageName(String name){

        //Check if name is just spaces or is empty
        if(validator(name)){
            return showError("Name is required");
        }

        //check if repo.existsByName(name) sends T or F. Sends false for now
         if(false){
             return showError("Name Already Exists");
         }

         //try to persist the name
         try{
             //repo.persist(name);
             Language newLanguage = new Language(name.trim().toLowerCase());
             return "Saved " + name + " successfully";
         }catch(Exception e){
             return showError("Couldn't save. Try Again");
         }
    }

    private boolean validator(String name){
        return name.trim().isEmpty();
    }

    private String showError(String error){
        return "ERROR: " + error;
    }

}
