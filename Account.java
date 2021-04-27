package socialmedia;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * The Account class defines the functionality of an Account through the relevant getters and setters
 * as well as additional helper methods
 *
 * @author Pierre Siddall and Priya Shah
 * @version 1.0
 */

public class Account implements Serializable {
    private ArrayList<Post> posts=new ArrayList<>();
    private ArrayList<Post> allPosts = new ArrayList<>();
    private String handle;
    private String description;
    private static int UNIQUE_ID = 0;
    private int uniqueID;


    /**
     * Constructor for an account with both
     * @param handle a String that is the Account's handle
     * @throws InvalidHandleException is thrown when setHandle results in an error
     */
    public Account(String handle) throws InvalidHandleException{
        setHandle(handle);
    }

    /**
     * Constructor
     * @param handle a String that is the Account's handle
     * @param description a String which is the Account's description
     * @throws InvalidHandleException is thrown when setHandle results in an error
     */
    public Account(String handle, String description) throws InvalidHandleException{
        setHandle(handle);
        if (!descriptionIsEmpty(description)){
            this.description = description;
        }

    }

    /**
     * Gets the handle of the Account
     * @return a String that is the handle of the Account
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Sets the handle of an Account
     * @param handle the String that the
     * @throws InvalidHandleException is thrown when handle is not blank, less than 30 chars and has no white spaces
     */
    public void setHandle(String handle) throws InvalidHandleException{
        if(handle.isBlank()){ //check if blank
           throw new InvalidHandleException("Handle is blank");
        }

        if(handle.length() <= 30) { //check if less than or equal to 30
            for (int i = 0; i < handle.length(); i++) {
                if (handle.charAt(i) == ' ') {
                    throw new InvalidHandleException("Handle cannot contain a white spaces");
                }

            }
            this.handle = handle;
            this.uniqueID = ++UNIQUE_ID;
        }
        else{
            throw new InvalidHandleException("Handle is greater than 30 character");
        }
    }

    /**
     * Sets the uniqueID of the account
     * @param uniqueId integer that will set the Account's unique ID
     */
    public static void setUniqueId(int uniqueId) {
        UNIQUE_ID = uniqueId;
    }

    /**
     * Gets the description of an Account
     * @return A string that is a description for a Account
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of an Account
     * @param description a String contains an Account's description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the ArrayList of Posts
     * @return the ArrayList of Posts made by an account
     */
    public ArrayList<Post> getPosts() {
        return posts;
    }

    /**
     * Sets the ArrayList of posts
     * @param posts the ArrayList of posts
     */
    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    /**
     * Gets the uniqueID of an Account
     * @return an integer containing the Account Object's ID
     */
    public int getUniqueID() {
        return this.uniqueID;
    }

    /**
     * Gets the ArrayList of AllPosts
     * @return the ArrayList of allPosts made by an account
     */
    public ArrayList<Post> getAllPosts() {
        return allPosts;
    }

    @Deprecated
    public void setAllPosts(ArrayList<Post> allPosts) {
        this.allPosts = allPosts;
    }

    /**
     * This method checks whether a Description is empty
     * @param Description A string that contains the description
     * @return  A boolean: returning true is the description is empty & false if otherwise
     */
    public boolean descriptionIsEmpty(String Description){
        if(Description == null || Description.isBlank()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Adds a post to the ArrayList of posts and the ArrayList of AllPosts
     * @param post the Post that the Account has created
     * @param account the Account that is associated with the post
     */
    public void addUserPost(Post post,Account account){
        posts.add(post); //adds posts to appropriate arrayList
        allPosts.add(post);
        post.setAccount(account); //adds a pointer back to the account
    }


    /**
     * Takes a comment and adds it to the ArrayList of AllPosts
     * @param comment the comment that the Account has made
     */
    public void addToAllPostsComment(Comment comment){
        allPosts.add(comment);
    }

    @Deprecated
    public void addToAllPostsEndorsement(Endorsement endorsement){
        allPosts.add(endorsement);
    }

    @Deprecated
    public boolean findPost(int id){
        for(Post post :posts){
            if(post.getSequentialID()==id){
                return true;
            }
        }return false;
    }

    /**
     * This method iterates through the ArrayList of AllPosts and returns a post
     * @param id the identifier of the Post
     * @return a Post object that the user has selected.
     */
    public Post getPost(int id){
        for(Post post :allPosts){
            if(post.getSequentialID()==id){
                return post;
            }
        }return null;
    }

    @Deprecated
    public void getComments(){
       for (Post post:posts){
           ArrayList<Comment> postComments=post.getComments();
           for(Comment comment:postComments){
               System.out.println(comment.toString());
           }
       }
    }


    @Deprecated
    public void replacePost(Post postToReplace,Post postToReplaceWith){
        allPosts.set(allPosts.indexOf(postToReplace), postToReplaceWith);
        posts.set(posts.indexOf(postToReplace),postToReplaceWith);
    }


    /**
     * Iterates through the ArrayList of AllPosts and counts the Endorsements
     * @return An integer of the no. endorsements that an Account has made
     */
    public int getEndorsements(){
        int counter = 0;
        for (Post post: allPosts){
            if(post instanceof Endorsement){
                ++counter;
            }
        }
        return counter;
    }


    /**
     * Makes an account redundant by setting the handle to be deleted and setting the description to null
     */
    public void makeAccountRedundant(){
        this.handle="<deleted>";
        this.description=null;
    }

    @Override
    public String toString(){
       return "ID: " + getUniqueID() + "\nHandle: " + getHandle() + "\nDescription: "+getDescription() + "\nPost count: " + allPosts.size() + "\nEndorse count: " + getEndorsements();
       //Todo - Post count and Endorse Count
    }
}