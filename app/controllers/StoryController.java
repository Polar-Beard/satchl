package controllers;

import actions.BasicAuth;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import daos.StoryDAO;
import daos.UserDAO;

import model.Story;

import java.util.List;
import java.util.UUID;

import model.User;
import play.mvc.*;
import play.libs.Json;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import services.HttpAuthorizationParser;

@JsonSerialize
public class StoryController extends Controller {
    private static final ObjectMapper objectMapper;
    private StoryDAO storyDAO;
    private UserDAO userDAO;

    static{
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public StoryController(){
        storyDAO = new StoryDAO();
        userDAO = new UserDAO();
    }

    @BasicAuth
    public Result addStory() {
        String userEmail = getEmailFromHeader();
        JsonNode storyAsJson = request().body().asJson();
        if (storyAsJson == null) {
            return badRequest("Expected JSON body");
        }
        Story story = Json.fromJson(storyAsJson, Story.class);
        User author = userDAO.getUser(userEmail);
        story.setAuthorId(author.getUserId());
        storyDAO.addStory(story);
        return ok("Story added to database");
    }

    public Result getStory(UUID storyId) {
        if (storyId == null) {
            return badRequest("Missing parameter [storyId]");
        }
        Story story = storyDAO.getStoryById(storyId);
        return ok(Json.toJson(story));
    }

    public Result getStories(int numberOfStories) {
        List<Story> stories = storyDAO.getStories(numberOfStories);
        if (stories.isEmpty()) {
            return badRequest("No stories found in database");
        }
        JsonNode jsonNode = objectMapper.valueToTree(stories);
        return ok(jsonNode);
    }

    @BasicAuth
    public Result getCurrentUserStories(){
        String userEmail = getEmailFromHeader();
        User currentUser = userDAO.getUser(userEmail);
        List<Story> stories = storyDAO.getStoriesByAuthorId(currentUser.getUserId());
        if (stories.isEmpty()) {
            return badRequest("No stories found for current user");
        }
        ArrayNode arrayNode = objectMapper.valueToTree(stories);
        arrayNode.add(Json.toJson(currentUser));
        return ok(arrayNode);
    }

    private String getEmailFromHeader(){
        HttpAuthorizationParser httpAuthorizationParser = new HttpAuthorizationParser();
        String[] credString = httpAuthorizationParser.getAuthorizationFromHeader(ctx());
        return credString[0];
    }
}
