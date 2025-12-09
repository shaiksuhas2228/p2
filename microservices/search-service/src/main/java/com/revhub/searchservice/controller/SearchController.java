package com.revhub.searchservice.controller;

import com.revhub.searchservice.document.PostDocument;
import com.revhub.searchservice.document.UserDocument;
import com.revhub.searchservice.dto.SearchRequest;
import com.revhub.searchservice.dto.SearchResponse;
import com.revhub.searchservice.service.SearchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping
    public ResponseEntity<SearchResponse> search(@Valid @RequestBody SearchRequest request) {
        SearchResponse response = searchService.search(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<SearchResponse.PostSearchResult>> searchPosts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<SearchResponse.PostSearchResult> results = searchService.searchPosts(query, page, size);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/users")
    public ResponseEntity<List<SearchResponse.UserSearchResult>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<SearchResponse.UserSearchResult> results = searchService.searchUsers(query, page, size);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/hashtag/{hashtag}")
    public ResponseEntity<List<SearchResponse.PostSearchResult>> searchByHashtag(
            @PathVariable String hashtag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<SearchResponse.PostSearchResult> results = searchService.searchByHashtag(hashtag, page, size);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/users/suggest")
    public ResponseEntity<List<SearchResponse.UserSearchResult>> suggestUsers(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<SearchResponse.UserSearchResult> results = searchService.searchUsersByPrefix(prefix, page, size);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/index/post")
    public ResponseEntity<String> indexPost(@RequestBody PostDocument post) {
        searchService.indexPost(post);
        return ResponseEntity.ok("Post indexed successfully");
    }

    @PostMapping("/index/user")
    public ResponseEntity<String> indexUser(@RequestBody UserDocument user) {
        searchService.indexUser(user);
        return ResponseEntity.ok("User indexed successfully");
    }

    @DeleteMapping("/index/post/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable String postId) {
        searchService.deletePost(postId);
        return ResponseEntity.ok("Post removed from index");
    }

    @DeleteMapping("/index/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        searchService.deleteUser(userId);
        return ResponseEntity.ok("User removed from index");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Search Service is running");
    }
}