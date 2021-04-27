package socialmedia;

import java.io.*;
import java.util.ArrayList;

/**
 * SocialMedia is a functioning implementor of
 * the SocialMediaPlatform interface.
 * 
 * @author Pierre Siddall and Priya Shah
 * @version 1.0
 */

public class SocialMedia implements SocialMediaPlatform {
	private ArrayList<Account> accounts = new ArrayList<>();

	@Override
	public int createAccount(String handle) throws IllegalHandleException, InvalidHandleException {
		if (!checkHandleUnique(handle)) { //Checks to see if a handle is not unique
			throw new IllegalHandleException("Handle is not unique");
		}
		Account newUser = new Account(handle);
		accounts.add(newUser);  //adds the account to the arraylist
		return newUser.getUniqueID();
	}

	@Override
	public int createAccount(String handle, String description) throws IllegalHandleException, InvalidHandleException {
		if (!checkHandleUnique(handle)) {//Checks to see if a handle is not unique
			throw new IllegalHandleException("Handle is not unique");
		}
		Account newUser = new Account(handle, description);
		accounts.add(newUser); //adds the account to the arraylist
		return newUser.getUniqueID();
	}


	@Override
	public void removeAccount(int id) throws AccountIDNotRecognisedException {
		for (Account account : accounts) {
			if (id == account.getUniqueID()) {
				for (int i = account.getAllPosts().size() - 1; i > 0; i--){ //iterates through all the posts of an account
					try {
						deletePost(account.getAllPosts().get(i).getSequentialID()); //calls the deletePost function
					} catch (PostIDNotRecognisedException e) {
						e.printStackTrace();
					}
				}
				account.makeAccountRedundant();
				accounts.remove(account); //removes the account from the array list
				return;
			}
		}
		throw new AccountIDNotRecognisedException("ID does not match any pre-existing IDs in the platform");
	}


	@Override
	public void removeAccount(String handle) throws HandleNotRecognisedException {
		for (Account account : accounts) {
			if (handle.equals(account.getHandle())) {//Check to see if the handle input is equal to the account handle
				for (int i = account.getAllPosts().size() - 1; i >= 0; i--){
					try{
						deletePost(account.getAllPosts().get(i).getSequentialID()); //calls the deletePost function
					} catch (PostIDNotRecognisedException e) {
						e.printStackTrace();
					}
				}
				account.makeAccountRedundant();
				accounts.remove(account); //removes account from the arraylist
				return;
			}
		}
		throw new HandleNotRecognisedException("Handle does not match any pre-existing handles in the platform");

	}

	@Override
	public void changeAccountHandle(String oldHandle, String newHandle) throws HandleNotRecognisedException, IllegalHandleException, InvalidHandleException {
		for (Account account : accounts) {
			if (!oldHandle.equals(account.getHandle())) {
				if (!checkHandleUnique(oldHandle)) { //checks if the handle is unqiue
					throw new IllegalHandleException("Handle is not unique");
				} else {
					account.setHandle(newHandle); //sets the handle
					return;
				}
			}
		}
		throw new HandleNotRecognisedException("Handle does not match any pre-existing handles in the platform");

	}

	@Override
	public void updateAccountDescription(String handle, String description) throws HandleNotRecognisedException {
		for (Account account : accounts) {
			if (handle.equals(account.getHandle())) { //checks if the handle is equal to the account
				if (!account.descriptionIsEmpty(description)) {
					account.setDescription(description);
					return;
				}
			}
		}
		throw new HandleNotRecognisedException("Handle does not match pre-existing handles in the platform");
	}

	@Override
	public String showAccount(String handle) throws HandleNotRecognisedException {
		for (Account account : accounts) {
			if (handle.equals(account.getHandle())) {
				return account.toString();
			}
		}
		throw new HandleNotRecognisedException("Handle does not match pre-existing handles in the platform");
	}


	@Override
	public int createPost(String handle, String message) throws HandleNotRecognisedException, InvalidPostException {
		if (!checkHandleUnique(handle)) {
			Post createPost = new Post(message);
			for (Account account : accounts) {
				if (handle.equals(account.getHandle())) {
					account.addUserPost(createPost, account); //Adds the new post to the appropriate ArrayLists
				}
			}
			return createPost.getSequentialID();
		} else {
			throw new HandleNotRecognisedException("Handle does not match pre-existing handles");
		}
	}


	@Override
	public int endorsePost(String handle, int id)
			throws HandleNotRecognisedException, PostIDNotRecognisedException, NotActionablePostException {
		if (id == 0){
			throw new PostIDNotRecognisedException("Post ID not recognised");
		}

		if (!checkHandleUnique(handle)) {
			for (Account account : accounts) { //Checks all posts in all accounts
				for (Post post : account.getAllPosts()) {
					if (id == post.getSequentialID()) {//Checks to see if the id input is the same a the id of the post currently being checked
						if (post instanceof Endorsement) {
							throw new NotActionablePostException("Endorsements cannot be endorsed");
						}
						Endorsement endorsement = new Endorsement(post, getAccount(handle));
						Post target_post = account.getPost(id);
						target_post.addToEndorsements(endorsement);
						Account owner_account = getAccount(handle);
						owner_account.getAllPosts().add(endorsement); //adds to the Account AllPost arraylist
						return endorsement.getSequentialID();
					}
				}
			}
			throw new PostIDNotRecognisedException("Post id does not match pre-existing post ID");
		} else {
			throw new HandleNotRecognisedException("Handle does not match pre-existing handles in the platform");
		}
	}

	@Override
	public int commentPost(String handle, int id, String message) throws HandleNotRecognisedException,
			PostIDNotRecognisedException, NotActionablePostException, InvalidPostException {
		if (checkHandleUnique(handle)) {
			throw new HandleNotRecognisedException("Handle does not match any pre-existing handles in the platform");

		}

		if (checkPostID(id)) { //checks if the ID is in the system
			throw new PostIDNotRecognisedException("Post ID not recognised");
		}

		if (checkNotEndorsement(id)) { //checks whether the ID does not belong to an endorsement
			throw new NotActionablePostException("ID is an endorsement which cannot have a child");
		}
		Comment comment = null;
		Account accountBy = getAccount(handle);
		for (Account account : accounts) {
			for (Post post : account.getAllPosts()) {//all posts in all accounts are checked
				if (id == post.getSequentialID()) {
					Post targetPost = account.getPost(id);
					comment = new Comment(accountBy, message, targetPost);
					targetPost.addToComments(comment);//The following two lines adds the comments to the appropriate arrays
					accountBy.addToAllPostsComment(comment);
					break;
				}
			}
		}
		return comment.getSequentialID();
	}


	@Override
	public void deletePost(int id) throws PostIDNotRecognisedException {
		//Checks if user ID is valid
		if (checkPostID(id)) {
			throw new PostIDNotRecognisedException("Post ID does not match pre-existing ids in the system");
		}

		//Checks if post is an orphan
		if (checkOrphan(id)) {
			Account deleteUserPost = getAccountByPostId(id);
			Post deletedPost = deleteUserPost.getPost(id);
			for (Account account : accounts) {//all posts in all accounts are checked
				for (Post post : account.getAllPosts()) {
					if (deletedPost.equals(post) && post.getComments().size() == 0) {
						try{
							//deletes all endorsements belonging to a post
							for (Endorsement e : deletedPost.getEndorsements()){
								e.getAccount().getAllPosts().remove(e);
								e.getPost().getEndorsements().remove(e);
								break;
							}
						}catch(NullPointerException e) {
							continue;
						}
						account.getAllPosts().remove(post);
						break;
					}
				}
			}
			if (deletedPost instanceof Endorsement){
				//deletes an endorsement
				Endorsement delete = (Endorsement) deletedPost;
				delete.getPost().getEndorsements().remove(delete);
			}

			deletedPost.getEndorsements().clear();
			deleteUserPost.getPosts().remove(deletedPost);
			deleteUserPost.getAllPosts().remove(deletedPost);
		} else {
			genWithChildren(id);
		}
	}


	@Override
	public String showIndividualPost(int id) throws PostIDNotRecognisedException {
		if(id==0){ //checks whether user has attempted to get an ID that begins with 0 and throws an error
			throw new PostIDNotRecognisedException("Post ID not recognised");
		}
		for (Account account : accounts) {//all posts in all accounts are checked
			for (Post post : account.getAllPosts()) {
				if (post.getSequentialID() == id) {
					return post.toString();
				}
			}
		}
		throw new PostIDNotRecognisedException("Post ID not recognised");
	}


	@Override
	public StringBuilder showPostChildrenDetails(int id) throws PostIDNotRecognisedException, NotActionablePostException {
		if (checkPostID(id)) { //checks whether post id is valid
			throw new PostIDNotRecognisedException("Post ID not recognised");
		}

		if (checkNotEndorsement(id)) { //checks whether the post selected is an endorsement
			throw new NotActionablePostException("Endorsement do not have children");
		}

		StringBuilder postChildren = new StringBuilder();
		postChildren.append("\t\t");
		for (Account account : accounts) {//all posts in all accounts are checked
			for (Post post : account.getAllPosts()) {
				if (post.getSequentialID() == id) {
					postChildren = post.getChildrenComments(1);
				}

			}
		}
		return postChildren;
	}

	@Override
	public int getNumberOfAccounts() {
		return accounts.size(); //returns the arraylist of the accounts;
	}


	@Override
	public int getTotalOriginalPosts() {
		int total = 0;
		for (Account account : accounts) {
			total=total+account.getPosts().size();
		}
		return total;
	}


	@Override
	public int getTotalEndorsmentPosts() {
		int total = 0;
		for (Account account : accounts) {//all posts in all accounts are checked
			for (Post post : account.getAllPosts()) {
				if(post instanceof Endorsement){
					total++;
				}
			}
		}
		return total;
	}

	@Override
	public int getTotalCommentPosts() {
		int total = 0;
		for (Account account : accounts) {//all posts in all accounts are checked
			for (Post post : account.getAllPosts()) {
				if(post instanceof Comment){
					total++;
				}
			}
		}
		return total;
	}


	@Override
	public int getMostEndorsedPost() {
		int most = 0;
		for (Account account : accounts) {//all posts in all accounts are checked
			for (Post post : account.getPosts()) {
				if (post.getEndorsements().size() > most) {
					most = post.getEndorsements().size();
				}
			}
		}
		return most;
	}


	@Override
	public int getMostEndorsedAccount() {
		try {
			Account most=null;
			int mostEndorsements=0;
			for(Account account:accounts){//all posts in all accounts are checked
				for(Post post:account.getPosts()){
					if(post.getEndorsements().size()>mostEndorsements){//If a post with more endorsements is found the account most is replaced
						mostEndorsements=post.getEndorsements().size();
						most=account;
					}
				}
			}
			return most.getUniqueID();
		} catch  (NullPointerException e){
			return 0;
		}

	}

	@Override
	public void erasePlatform() {
		for (Account account:accounts){//all posts in all accounts are checked
			for(Post post:account.getPosts()){
				post.setSEQID(0);
			}
		}
		for(int i = 0; i < accounts.size(); i++){
			accounts.get(i).getAllPosts().clear();
			accounts.get(i).setUniqueId(0);
			accounts.remove(accounts.get(i));

			i--;
		}

	}


	@Override
	public void savePlatform(String filename) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(filename);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(accounts);
		out.close();
		fileOut.close();
	}


	@Override
	public void loadPlatform(String filename) throws IOException, ClassNotFoundException {
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
			Object obj = in.readObject();
			if (obj instanceof ArrayList) {
				accounts = (ArrayList<Account>) obj;
			}
		}
	}

	/**
	 * Checks if the Handle given is unique
	 * @param suggestedHandle a String that is suggested by an Account
	 * @return true if the handle is Unique, and false if it is not unique
	 */
	public boolean checkHandleUnique(String suggestedHandle) {
		for (Account account : accounts) { //compares all handles in an account to the suggested handle
			if (suggestedHandle.equals(account.getHandle())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the Account
	 * @param handle a String that identifies an Account
	 * @return an Account object with the same handle
	 */
	public Account getAccount(String handle) {
		for (Account account : accounts) {
			if (account.getHandle().equals(handle)) {
				return account;
			}
		}
		return null;
	}

	/**
	 * Checks whether the post ID matches a pre-exisitng Post in the system.
	 * @param id the integer that identifies a Post
	 * @return a boolean: true if found or false if not
	 * @throws PostIDNotRecognisedException
	 */
	public boolean checkPostID(int id) throws PostIDNotRecognisedException {
		if (id == 0){ //checks whether user has attemtped to input 0
			throw new PostIDNotRecognisedException("Post Id not recognised");
		}

		for (Account account : accounts) {
			for (Post post : account.getAllPosts()) { //iterates through each account's posts
				if (post.getSequentialID() == id) {
					return false;
				}
			}
		}
		return true;
	}

	@Deprecated
	public boolean checkNotEndorsement(int id) {
		for (Account account : accounts) {
			for (Post post : account.getAllPosts()) {
				if (post.getSequentialID() == id) {
					if (post instanceof Endorsement) {
						return true;
					}
				}
			}
		}

		return false;
	}


	/**
	 * Checks if a post has any children or not
	 * @param id takes an integer that identifies a Post by their unique ID
	 * @return a boolean: true if it is an orphan and false if not.
	 */
	public boolean checkOrphan(int id) {
		for (Account account : accounts) {
			for (Post post : account.getAllPosts()) { //iterates through each accounts post
				if (post.getSequentialID() == id) {
					if (post.getComments().size() == 0) {//Checks to see if there are no comments on a post
						return true;
					}
				}

			}
		}
		return false;
	}

	/**
	 * This iterates through Accounts and Post to find an Account associated with a Post
	 * @param id an integer that contains the unique ID of the post
	 * @return an Account with that Post
	 */
	public Account getAccountByPostId(int id) {
		for (Account account : accounts) {
			for (Post post : account.getAllPosts()) { //iterates through each accounts posts
				if (post.getSequentialID() == id) {
					return account;
				}
			}
		}
		return null;
	}


	/**
	 * This method softly deletes (not hard deletion) of a comment of post with children and remove endorsements
	 * @param id takes the ID of the post that gets deleted
	 */
	public void genWithChildren(int id) {
		for (Account account : accounts) {
			for (Post post : account.getAllPosts()) {
				if (post.getSequentialID() == id) {
					if (post instanceof Comment) { //checks whether post is a child
						Comment change = (Comment) post;
						//removes endorsements from ArrayList
						for (Endorsement e : post.getEndorsements()) {
							e.getAccount().getAllPosts().remove(e);
						}
						// endorsements ArrayList is cleared
						post.getEndorsements().clear();
						//Changes the message of a comment
						change.setMessage("The original content was removed from the system and is no longer available.");
						change.setSequentialID(0);
					}
					else {
						for (Endorsement e : post.getEndorsements()) { //Iterates through the endorsements of a post one by one
							e.getAccount().getAllPosts().remove(e);
						}
						//Makes post redundant
						post.getEndorsements().clear();
						post.setSequentialID(0);
						post.setMessage("The original content was removed from the system and is no longer available.");
						account.getAllPosts().remove(post); //Removes the post from the appropriate ArrayList
						account.getPosts().remove(post); //Removes post from account arrayList
						break;
					}
				}
			}
		}
	}
}

