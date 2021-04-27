package socialmedia;

/**
 *
 * This class defines the functionality of an endorsement through the relevant getter and
 * setter methods
 *
 * @author Pierre Siddall and Priya Shah
 * @version 1.0
 */
public class Endorsement extends Post{
    private  Post post;
    private String message;
    private Account account;
    private boolean hasEndorsedPost=true;

    /**
     * Constructor that is used to create an endorsement
     * @param post the post that is being endorsed
     * @param account the account that is endorsing the post
     */
    public Endorsement(Post post, Account account){
        sequentialID = ++SEQID;
        this.post=post;
        this.message = post.getMessage();
        this.account = account;
    }

    /**
     * Gets the Post
     * @return a Post object that an Endorsement is referring to
     */
    public Post getPost() {
        return post;
    }

    @Deprecated
    public boolean isHasEndorsedPost() {
        return hasEndorsedPost;
    }


    @Override
    public Account getAccount() {
        return account;
    }

    @Deprecated
    public void setHasEndorsedPost(boolean hasEndorsedPost) {
        this.hasEndorsedPost = hasEndorsedPost;
    }


    @Override
    public String toString(){
        return "EP@" + account.getHandle() + ": " + message;
    }
}
