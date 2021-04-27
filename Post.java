package socialmedia;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * This class defines the functionality of a post in which various getter and setter
 * methods for attributes are defined alongside the methods which generate whitespace to
 * be added in the toString method of post and returns the toString() of a posts children
 *
 *  @author Pierre siddall and Priya Shah
 *  @version 1.0
 * */

public class Post implements Serializable {
    protected Account account=null;
    private String message;
    protected static int SEQID=0;
    protected int sequentialID;
    protected ArrayList<Comment> comments=new ArrayList<>();
    protected ArrayList<Endorsement> endorsements = new ArrayList<>();
    private int counter=0;


    /**
     * A constructor for the post class which defines a generic post (a post which has been deleted)
     */
    public Post(){
        this.message="The original content was removed from the system and is no longer available.";
        this.sequentialID=0;
    }

    /**
     * A constructor for the post class that defines a new post with a message defined by the user. The sequential id of the post
     * is updated appropriately to make it unique
     * @param message - A string that contains the message of the post
     * @throws InvalidPostException
     */
    public Post(String message) throws InvalidPostException{
        if (message.isBlank()){
            throw new InvalidPostException("Message cannot be blank");
        }
        else if (message.length() > 100){
            throw new InvalidPostException("Message exceeds 100 characters");
        }
        else{
            this.message = message;
            this.sequentialID = ++SEQID;
        }
    }

    /**
     * Sets the sequential id of the post
     * @param SEQID a static integer
     *
     */
    public static void setSEQID(int SEQID) {
        Post.SEQID = SEQID;
    }


    /**
     * This gets the message of the post
     * @return A string that is a Message of the post
     *
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message of the post
     * @param message a String that contains the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the sequential ID of a post
     * @return An integer that contains the sequential ID of a post
     */
    public int getSequentialID() {
        return sequentialID;
    }

    /**
     * This sets the sequential ID of a post
     * @param sequentialID an integer containing the sequential ID of the post
     */
    public void setSequentialID(int sequentialID){
        this.sequentialID = sequentialID;
    }

    /**
     * Gets the account object associated with a post
     * @return The account associated with a post
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account associated with a post
     * @param account an account associated with a post
     *
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * The ArrayList of comments associated with a post
     * @return an ArrayList of comments
     */
    public ArrayList<Comment> getComments() {
        return comments;
    }

    /**
     * Sets the ArrayList of comments associated with a post
     * @param comments - An ArrayList of comments
     */
    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Gets the Endorsement ArrayList associated with a post
     * @return The ArrayList of endorsements
     */
    public ArrayList<Endorsement> getEndorsements() {
        return endorsements;
    }

    @Deprecated
    public void setEndorsements(ArrayList<Endorsement> endorsements) {
        this.endorsements = endorsements;
    }

    /**
     * Adds a comment to the comments ArrayList
     * @param comment - A comment object that will be added to the ArrayList
     */
    public void addToComments(Comment comment){
        comments.add(comment);
    }

    /**
     * Adds a endorsement to the endorsements ArrayList
     * @param endorsement - An Endorsement object to be added to ArrayList
     */
    public void addToEndorsements(Endorsement endorsement){
        endorsements.add(endorsement);
    }

    @Deprecated
    public boolean checkEndorsements(int id){
        for (Endorsement endorsement:endorsements){
            if(endorsement.getSequentialID() == id){
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the whitespaces to use in the to stringBuilder
     * @param count an integer that calculates the number of whitespaces
     * @return A string containing the appropriate amount of whitespace
     */
    public String whiteSpaceGen(int count){
        String whitespace="";
        for (int i=0; i<=count;i++){
            whitespace=whitespace+"\t"; //Adds whitespace to the whitespace string
        }
        return whitespace;
    }

    /**
     * Recursive method to create a StringBuilder of the children comments
     * @param count an integer that keeps track of the tab number
     * @return The StringBuilder of the children comments
     */
    public StringBuilder getChildrenComments(int count){
        counter = count;
        count++;
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t"+this + "\n"); //appends the toString() of this instance of Post
        for (Comment comment : getComments()) {
            sb.append(whiteSpaceGen(counter)+"|\n"+whiteSpaceGen(counter)+"| >");//generates the tabs of children
            sb.append(comment.getChildrenComments(count));// Recursive call to the children of a post adding the toString() to the StringBuilder
        }
        return sb;
    }

    @Deprecated
    public void makePostRedundant(){
        this.account=null;
        this.endorsements=null;

    }

    @Override
    public String toString(){
        String ws=whiteSpaceGen(counter); //Appropriate whitespace calculated
        StringBuilder tab = new StringBuilder();
        tab.append("ID: " + getSequentialID() + "\n"+ws+"Account: "+account.getHandle()+"\n"+ws+"No. endorsements: " + endorsements.size()+ " | No. comments: " + comments.size() + "\n"+ws+getMessage());
        return tab.toString();

    }
}

