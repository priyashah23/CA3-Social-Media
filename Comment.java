package socialmedia;

/**
 *
 *This class defines the functionality of a comment through the relevant getter and
 * setter methods
 *
 * @author Pierre Siddall and Priya Shah
 * @version  1.0
 * */
public class Comment extends Post{
    private Post post;
    /**
     * Constructor that is used to create a comment
     * @param account the account that is making a comment on a post
     * @param message the message that
     * @param post the post that is being commented on
     * @throws InvalidPostException when the message is not valid
     */
    public Comment(Account account, String message, Post post) throws InvalidPostException{
        super(message);
        this.post = post;
        this.account = account;
    }

    /**
     * Gets the post that the comment is referring to
     * @return The Post object
     */
    public Post getPost() {
        return post;
    }

    /**
     * Sets the post object
     * @param post A post object that the comment is referring to
     */
    public void setPost(Post post) {
        this.post = post;
    }


    @Override
    public int getSequentialID() {
        return super.getSequentialID();
    }


    /**
     *
     * @param count
     * @return The string version of a comment in the platform
     * Takes in a count parameter to generate the appropriate amount of whitespace to be used in the string
     */
    public String toString(int count) {
        String whitespace=whiteSpaceGen(count);
        return whitespace+"| > ID: " + getSequentialID()+"\n"+whitespace+"Account: " + account.getHandle() +"\n"+whitespace+"No. endorsements:" + endorsements.size() + " | No. comments: " + comments.size() +"\n"+whitespace+ getMessage();
    }
}
