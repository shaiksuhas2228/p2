import { Component, OnInit, HostListener, ViewChild, ElementRef } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AvatarComponent } from '../shared/components/avatar.component';
import { ThemeService } from '../core/services/theme.service';
import { FeedService, Post } from '../core/services/feed.service';
import { AuthService } from '../core/services/auth.service';
import { ProfileService, User } from '../core/services/profile.service';
import { PostService } from '../core/services/post.service';
import { ChatService, ChatMessage } from '../core/services/chat.service';
import { NotificationService, Notification } from '../core/services/notification.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule, AvatarComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  @ViewChild('chatContainer') private chatContainer?: ElementRef;
  activeTab = 'feed';
  feedType = 'universal';
  activeFeedType = 'universal';
  currentPage = 0;
  hasMorePosts = true;
  isLoading = false;
  isDarkTheme = false;
  isEditingProfile = false;
  editBio = '';
  selectedProfilePicture: File | null = null;
  profileName = '';
  profileUsername = '';
  currentUser: any = null;
  userProfile: User | null = null;
  followersCount = 0;
  followingCount = 0;
  userPostsData: any[] = [];
  newPostContent = '';
  posts: any[] = [
    {
      id: 1,
      author: 'Akram',
      content: 'This is a sample post content.',
      timestamp: '2 hours ago',
      likes: 5,
      comments: 2,
      shares: 1,
      liked: false,
      media: null,
      mediaType: '',
      commentsList: [
        { id: 1, author: 'Karthik', content: 'Great post!', timestamp: '1 hour ago' },
        { id: 2, author: 'Akram', content: 'Thanks!', timestamp: '30 min ago' }
      ]
    }
  ];
  
  selectedFile: File | null = null;
  selectedFileType = '';
  selectedFilePreview: string | null = null;
  showComments: { [key: number]: boolean } = {};
  newComment = '';
  selectedPostId: number | null = null;
  replyingTo: { postId: string, commentId: number } | null = null;
  replyContent = '';
  postVisibility = 'public';
  
  selectedChat: string | null = null;
  selectedContactData: any = null;
  newMessage = '';
  contacts: any[] = [];
  messages: { [key: string]: any[] } = {};
  chatSearchQuery = '';
  chatSearchResults: any[] = [];
  unreadCounts: { [key: string]: number } = {};
  Object = Object;

  constructor(
    private themeService: ThemeService, 
    private feedService: FeedService,
    private authService: AuthService,
    private profileService: ProfileService,
    private postService: PostService,
    private chatService: ChatService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    console.log('🚀 Dashboard component initializing...');
    
    this.themeService.isDarkTheme$.subscribe(isDark => {
      this.isDarkTheme = isDark;
    });
    
    // Load current user data
    this.currentUser = this.authService.getCurrentUser();
    console.log('👤 Current user from auth service:', this.currentUser);
    
    if (this.currentUser) {
      console.log('✅ User authenticated, loading profile data...');
      this.profileName = this.currentUser.username;
      this.profileUsername = this.currentUser.username;
      this.loadUserProfile();
      
      // Load MongoDB notifications and chat data
      console.log('📱 Loading notifications and chat data...');
      this.loadNotifications();
      
      // Subscribe to WebSocket messages
      this.chatService.subscribeToMessages(this.currentUser.username);
      this.chatService.messages$.subscribe(message => {
        console.log('Received WebSocket message:', message);
        const otherUser = message.senderUsername === this.currentUser?.username ? message.receiverUsername : message.senderUsername;
        
        // Only add message if it's from the other user (not our own sent message)
        if (message.senderUsername !== this.currentUser?.username) {
          // Update messages if chat is open
          if (this.selectedChat === otherUser) {
            if (!this.messages[this.selectedChat]) {
              this.messages[this.selectedChat] = [];
            }
            this.messages[this.selectedChat].push({
              sender: message.senderUsername,
              content: message.content,
              timestamp: new Date(message.timestamp).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})
            });
            setTimeout(() => this.scrollToBottom(), 100);
          }
          
          // Update unread counts and contacts
          if (message.receiverUsername === this.currentUser?.username && this.selectedChat !== message.senderUsername) {
            this.unreadCounts[message.senderUsername] = (this.unreadCounts[message.senderUsername] || 0) + 1;
            if (!this.contacts.includes(message.senderUsername)) {
              this.contacts.unshift(message.senderUsername);
            }
          }
        }
      });
    } else {
      console.error('❌ No authenticated user found!');
    }
    
    // Only load feeds if activeTab is 'feed'
    if (this.activeTab === 'feed') {
      this.loadFeeds();
    }
    this.loadSuggestedUsers();
  }

  loadFeeds() {
    this.isLoading = true;
    this.postService.getPosts(0, 10).subscribe({
      next: (response) => {
        this.posts = response.content || [];
        this.currentPage = response.number || 0;
        this.hasMorePosts = (response.number || 0) < (response.totalPages || 0) - 1;
        this.isLoading = false;
      },
      error: (error) => {
        this.posts = [];
        this.isLoading = false;
      }
    });
  }

  switchFeedType(type: string) {
    this.feedType = type;
    this.loadFeeds();
  }
  
  switchFeed(feedType: string) {
    this.activeFeedType = feedType;
    this.feedType = feedType;
    this.currentPage = 0;
    this.posts = [];
    this.loadFeedsByType(feedType);
  }
  
  loadFeedsByType(feedType: string) {
    this.isLoading = true;
    
    if (feedType === 'followers' && this.currentUser) {
      // Load posts only from users you follow
      this.profileService.getFollowing(this.currentUser.username).subscribe({
        next: (following) => {
          if (following.length === 0) {
            this.posts = [];
            this.isLoading = false;
            return;
          }
          
          // Get posts from all followed users
          const followedUserIds = following.map(u => u.id);
          this.postService.getPosts(0, 100).subscribe({
            next: (response) => {
              this.posts = (response.content || []).filter((post: any) => 
                followedUserIds.includes(post.userId) || post.author?.id === this.currentUser?.id
              );
              this.isLoading = false;
            },
            error: () => {
              this.posts = [];
              this.isLoading = false;
            }
          });
        },
        error: () => {
          this.posts = [];
          this.isLoading = false;
        }
      });
    } else {
      // Load all posts (universal feed)
      this.postService.getPosts(0, 10, feedType).subscribe({
        next: (response) => {
          this.posts = response.content || [];
          this.currentPage = response.number || 0;
          this.hasMorePosts = (response.number || 0) < (response.totalPages || 0) - 1;
          this.isLoading = false;
        },
        error: (error) => {
          this.posts = [];
          this.isLoading = false;
        }
      });
    }
  }

  loadMorePosts() {
    if (this.isLoading || !this.hasMorePosts || this.feedType === 'universal' || !this.followingList) return;
    
    this.isLoading = true;
    const followingNames = this.followingList.map(f => f.username);
    const morePosts = this.feedService.loadMorePosts(followingNames);
    
    if (morePosts.length > 0) {
      this.posts = [...this.posts, ...morePosts];
    } else {
      this.hasMorePosts = false;
    }
    
    this.isLoading = false;
  }

  getTotalUnreadCount(): number {
    return Object.values(this.unreadCounts).reduce((sum: number, count: any) => sum + (count || 0), 0);
  }

  scrollToBottom(): void {
    try {
      if (this.chatContainer) {
        this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
      }
    } catch(err) { }
  }
  
  setActiveTab(tab: string) {
    console.log('📱 Switching to tab:', tab);
    this.activeTab = tab;
    
    setTimeout(() => {
      if (tab === 'feed') {
        this.showSuggestions = true;
        this.loadFeeds();
      } else if (tab === 'profile') {
        console.log('👤 Loading profile tab...');
        this.loadUserProfile();
      } else if (tab === 'notifications') {
        console.log('🔔 Loading notifications tab...');
        this.loadNotifications();
      } else if (tab === 'chat') {
        console.log('💬 Loading chat tab...');
        if (this.followingList.length === 0) {
          console.log('👥 Loading following list for chat...');
          this.loadFollowing();
        }
        this.loadChatContacts();
      }
    }, 0);
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }

  editProfile() {
    this.isEditingProfile = true;
    this.editBio = this.userProfile?.bio || '';
  }

  saveProfile() {
    const updates: any = {
      username: this.currentUser?.username,
      bio: this.editBio
    };
    
    if (this.selectedProfilePicture) {
      const reader = new FileReader();
      reader.onload = (e) => {
        updates.profilePicture = e.target?.result as string;
        this.updateProfile(updates);
      };
      reader.readAsDataURL(this.selectedProfilePicture);
    } else {
      this.updateProfile(updates);
    }
  }
  
  updateProfile(updates: any) {
    this.profileService.updateProfile(updates).subscribe({
      next: (updatedUser) => {
        console.log('Profile updated successfully');
        if (updatedUser.profilePicture) {
          const user = JSON.parse(localStorage.getItem('user') || '{}');
          user.profilePicture = updatedUser.profilePicture;
          localStorage.setItem('user', JSON.stringify(user));
        }
        this.isEditingProfile = false;
        this.selectedProfilePicture = null;
        setTimeout(() => {
          this.loadUserProfile();
          this.loadFeeds();
        }, 500);
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        alert('Failed to update profile. Please try again.');
      }
    });
  }
  
  updateProfileWithFile(formData: FormData) {
    this.profileService.updateProfileWithFile(formData).subscribe({
      next: (updatedUser) => {
        this.loadUserProfile(); // Reload profile data
        this.isEditingProfile = false;
        this.selectedProfilePicture = null;
      },
      error: (error) => {
        console.error('Error updating profile with file:', error);
        // Fallback to just bio update if file upload fails
        if (this.editBio !== (this.userProfile?.bio || '')) {
          const updates = { bio: this.editBio };
          this.updateProfile(updates);
        } else {
          this.isEditingProfile = false;
        }
      }
    });
  }

  cancelEdit() {
    this.isEditingProfile = false;
    this.editBio = '';
    this.selectedProfilePicture = null;
  }
  
  onProfilePictureSelected(event: any) {
    if (!event?.target?.files || event.target.files.length === 0) {
      return;
    }
    
    const file = event.target.files[0];
    if (file && file.type.startsWith('image/')) {
      this.selectedProfilePicture = file;
    }
  }

  createPost() {
    if (this.newPostContent.trim() && this.currentUser) {
      let finalContent = this.newPostContent;
      
      if (this.hashtagInput.trim()) {
        const hashtags = this.hashtagInput.split(',').map(tag => tag.trim()).filter(tag => tag);
        const hashtagString = hashtags.map(tag => tag.startsWith('#') ? tag : `#${tag}`).join(' ');
        finalContent += ' ' + hashtagString;
      }
      
      if (this.locationTag.trim()) {
        finalContent += ` 📍 ${this.locationTag}`;
      }
      
      if (this.showPollCreation && this.pollOptions.some(option => option.trim())) {
        const validOptions = this.pollOptions.filter(option => option.trim());
        if (validOptions.length >= 2) {
          finalContent += '\n\n📊 Poll:';
          validOptions.forEach((option, index) => {
            finalContent += `\n${index + 1}. ${option}`;
          });
        }
      }
      
      if (this.selectedFile) {
        const formData = new FormData();
        formData.append('content', finalContent);
        formData.append('file', this.selectedFile);
        formData.append('userId', this.currentUser.id.toString());
        
        this.postService.createPostWithFile(formData).subscribe({
          next: (response) => {
            this.resetPostForm();
          },
          error: (error) => {
            console.error('Error creating post:', error);
          }
        });
      } else {
        const postData = {
          content: finalContent,
          userId: this.currentUser.id,
          imageUrl: ''
        };
        
        this.postService.createPost(postData).subscribe({
          next: (response) => {
            this.resetPostForm();
          },
          error: (error) => {
            console.error('Error creating post:', error);
          }
        });
      }
    }
  }
  
  resetPostForm() {
    this.newPostContent = '';
    this.hashtagInput = '';
    this.selectedFile = null;
    this.selectedFileType = '';
    this.selectedFilePreview = null;
    this.postVisibility = 'public';
    this.showPollCreation = false;
    this.showLocationTag = false;
    this.pollOptions = ['', ''];
    this.locationTag = '';
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
    this.setActiveTab('feed');
    setTimeout(() => {
      this.loadFeeds();
      this.loadUserProfile();
    }, 500);
  }

  onFileSelected(event: any) {
    if (!event?.target?.files || event.target.files.length === 0) {
      return;
    }
    
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      if (file.type.startsWith('image/')) {
        this.selectedFileType = 'image';
      } else if (file.type.startsWith('video/')) {
        this.selectedFileType = 'video';
      }
      
      const reader = new FileReader();
      reader.onload = (e) => {
        this.selectedFilePreview = e.target?.result as string;
      };
      reader.onerror = (error) => {
        console.error('Error reading file:', error);
        this.selectedFile = null;
        this.selectedFileType = '';
        this.selectedFilePreview = null;
      };
      reader.readAsDataURL(file);
    }
  }

  likePost(post: any) {
    this.postService.toggleLike(post.id).subscribe({
      next: (response) => {
        post.likesCount = response.likesCount;
        post.isLiked = response.isLiked;
      },
      error: (error) => {
        // Handle error
      }
    });
  }

  commentPost(post: any) {
    this.showComments[post.id] = !this.showComments[post.id];
    if (!post.commentsList) {
      post.commentsList = [];
    }
    if (this.showComments[post.id]) {
      this.postService.getComments(post.id).subscribe({
        next: (comments) => {
          post.commentsList = comments;
        },
        error: (error) => {
          // Handle error
        }
      });
    }
  }

  sharePost(post: any) {
    const shareData = {
      title: 'RevHub Post',
      text: `Check out this post by ${post.author.username}: ${post.content}`,
      url: window.location.href
    };

    if (navigator.share) {
      navigator.share(shareData).then(() => {
        this.postService.sharePost(post.id, this.currentUser?.id).subscribe({
          next: (response) => {
            post.sharesCount = response.sharesCount;
          },
          error: (error) => {
            // Handle error
          }
        });
      }).catch((error) => {
        // Handle error
      });
    } else {
      // Fallback for browsers that don't support Web Share API
      const text = `Check out this post by ${post.author.username}: ${post.content}`;
      const whatsappUrl = `https://wa.me/?text=${encodeURIComponent(text)}`;
      window.open(whatsappUrl, '_blank');
      
      this.postService.sharePost(post.id, this.currentUser?.id).subscribe({
        next: (response) => {
          post.sharesCount = response.sharesCount;
        },
        error: (error) => {
          console.error('Error updating share count:', error);
        }
      });
    }
  }

  fallbackShare(post: any) {
    const text = `Check out this post by ${post.author}: ${post.content}`;
    const whatsappUrl = `https://wa.me/?text=${encodeURIComponent(text)}`;
    window.open(whatsappUrl, '_blank');
    post.shares += 1;
  }

  formatPostContent(content: string): string {
    return content
      .replace(/#(\w+)/g, '<span class="hashtag">#$1</span>')
      .replace(/@(\w+)/g, '<span class="mention">@$1</span>');
  }

  addComment(post: any) {
    if (this.newComment.trim()) {
      this.postService.addComment(post.id, this.newComment).subscribe({
        next: (response) => {
          if (!post.commentsList) {
            post.commentsList = [];
          }
          post.commentsList.push(response);
          post.commentsCount = post.commentsList.length;
          this.newComment = '';
        },
        error: (error) => {
          // Handle error
        }
      });
    }
  }

  deleteComment(post: any, commentId: number) {
    this.commentToDelete = { post, commentId };
    this.showDeleteCommentConfirm = true;
  }

  confirmDeleteComment() {
    if (this.commentToDelete) {
      const { post, commentId } = this.commentToDelete;
      this.postService.deleteComment(post.id, commentId).subscribe({
        next: (response) => {
          this.postService.getComments(post.id).subscribe({
            next: (comments) => {
              post.commentsList = comments;
              post.commentsCount = comments.length;
            },
            error: (error) => {
              // Handle error
            }
          });
          this.showDeleteCommentConfirm = false;
          this.commentToDelete = null;
        },
        error: (error) => {
          this.showDeleteCommentConfirm = false;
          this.commentToDelete = null;
        }
      });
    }
  }

  cancelDeleteComment() {
    this.showDeleteCommentConfirm = false;
    this.commentToDelete = null;
  }

  canDeleteComment(comment: any, post: any): boolean {
    return comment.author?.username === this.currentUser?.username || post.author?.username === this.currentUser?.username;
  }

  replyToComment(post: any, comment: any) {
    this.replyingTo = { postId: post.id, commentId: comment.id };
  }

  addReply(post: any, parentComment: any) {
    if (this.replyContent.trim()) {
      const reply = {
        id: Date.now(),
        author: this.profileName,
        content: this.replyContent,
        timestamp: 'Just now',
        isReply: true,
        parentId: parentComment.id
      };
      if (!parentComment.replies) {
        parentComment.replies = [];
      }
      parentComment.replies.push(reply);
      this.replyContent = '';
      this.replyingTo = null;
    }
  }

  cancelReply() {
    this.replyingTo = null;
    this.replyContent = '';
  }



  followUser(user: any) {
    console.log('Current user:', this.currentUser);
    console.log('Following user:', user.username);
    
    if (user.username === this.currentUser?.username) {
      alert('You cannot follow yourself');
      return;
    }
    
    this.profileService.followUser(user.username).subscribe({
      next: (response) => {
        console.log('Follow success:', response.message);
        if (response.message.includes('request sent')) {
          user.followStatus = 'PENDING';
        } else {
          user.followStatus = 'ACCEPTED';
          this.followingCount++;
          this.loadFollowing();
        }
        this.loadUserProfile();
        this.suggestedUsers = this.suggestedUsers.filter(u => u.username !== user.username);
      },
      error: (error) => {
        console.error('Error following user:', error);
        if (error.error?.message) {
          alert(error.error.message);
        }
      }
    });
  }
  
  cancelFollowRequest(user: any) {
    this.profileService.cancelFollowRequest(user.username).subscribe({
      next: (response) => {
        console.log(response.message);
        user.followStatus = 'NOT_FOLLOWING';
        this.loadUserProfile();
        // Update suggested users follow status
        this.updateSuggestedUserStatus(user.username, 'NOT_FOLLOWING');
      },
      error: (error) => {
        console.error('Error cancelling follow request:', error);
        // Fallback: still update UI to prevent stuck state
        user.followStatus = 'NOT_FOLLOWING';
        this.updateSuggestedUserStatus(user.username, 'NOT_FOLLOWING');
      }
    });
  }

  followFromList(user: any) {
    this.profileService.followUser(user.username).subscribe({
      next: (response) => {
        console.log(response.message);
        user.followStatus = 'ACCEPTED';
        this.loadUserProfile();
      },
      error: (error) => {
        console.error('Error following user:', error);
      }
    });
  }

  unfollowUser(user: any) {
    console.log('Attempting to unfollow user:', user.username);
    this.profileService.unfollowUser(user.username).subscribe({
      next: (response) => {
        console.log('Unfollow success:', response.message);
        user.followStatus = 'NOT_FOLLOWING';
        this.followingCount = Math.max(0, this.followingCount - 1);
        
        // Delete chat conversation
        this.chatService.deleteConversation(user.username, this.currentUser?.id).subscribe({
          next: () => {
            console.log('Chat deleted with:', user.username);
            this.contacts = this.contacts.filter(c => c !== user.username);
            delete this.unreadCounts[user.username];
            if (this.selectedChat === user.username) {
              this.selectedChat = null;
            }
          },
          error: (error) => {
            console.error('Error deleting chat:', error);
          }
        });
        
        if (this.profileActiveTab === 'following') {
          this.loadFollowing();
        }
        setTimeout(() => this.loadUserProfile(), 500);
        this.updateSuggestedUserStatus(user.username, 'NOT_FOLLOWING');
      },
      error: (error) => {
        console.error('Error unfollowing user:', error);
      }
    });
  }

  isFollowing(user: any): boolean {
    return user.followStatus === 'ACCEPTED';
  }

  closeSuggestions() {
    this.showSuggestions = false;
  }

  selectChat(contact: any) {
    const username = typeof contact === 'string' ? contact : contact.username;
    this.selectedChat = username;
    this.selectedContactData = typeof contact === 'string' ? null : contact;
    this.loadConversation(username);
    // Mark messages as read and reset unread count
    this.chatService.markAsRead(username, this.currentUser?.id).subscribe({
      next: () => {
        this.unreadCounts[username] = 0;
      }
    });
  }
  
  loadConversation(username: string) {
    this.chatService.getConversation(username, this.currentUser?.id).subscribe({
      next: (messages) => {
        this.messages[username] = messages
          .sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime())
          .map(msg => ({
            sender: msg.senderUsername,
            content: msg.content,
            timestamp: new Date(msg.timestamp).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})
          }));
        console.log('Loaded conversation with', username, ':', messages.length, 'messages');
        setTimeout(() => this.scrollToBottom(), 100);
      },
      error: (error) => {
        console.error('Error loading conversation:', error);
        this.messages[username] = [];
      }
    });
  }

  sendMessage() {
    if (this.newMessage.trim() && this.selectedChat) {
      const messageContent = this.newMessage.trim();
      console.log('Sending message to', this.selectedChat, ':', messageContent);
      
      // Immediately add message to UI
      if (!this.messages[this.selectedChat]) {
        this.messages[this.selectedChat] = [];
      }
      this.messages[this.selectedChat].push({
        sender: this.currentUser?.username,
        content: messageContent,
        timestamp: new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})
      });
      
      const tempMessage = messageContent;
      this.newMessage = '';
      setTimeout(() => this.scrollToBottom(), 100);
      
      this.chatService.sendMessage(this.selectedChat, tempMessage, this.currentUser?.id, this.currentUser?.username).subscribe({
        next: (message) => {
          console.log('Message sent successfully:', message);
          
          // Add contact to list if not present
          const existingContact = this.contacts.find(c => 
            (typeof c === 'string' ? c : c.username) === this.selectedChat
          );
          if (!existingContact) {
            // Fetch profile to get picture
            this.profileService.getProfile(this.selectedChat!).subscribe({
              next: (profile) => {
                this.contacts.unshift({
                  username: profile.username,
                  profilePicture: profile.profilePicture || ''
                });
              },
              error: () => {
                this.contacts.unshift({
                  username: this.selectedChat!,
                  profilePicture: ''
                });
              }
            });
          }
        },
        error: (error) => {
          console.error('Error sending message:', error);
          alert('Failed to send message. Please try again.');
          // Remove message from UI on error
          if (this.messages[this.selectedChat!]) {
            this.messages[this.selectedChat!] = this.messages[this.selectedChat!].filter(
              m => m.content !== tempMessage || m.sender !== this.currentUser?.username
            );
          }
        }
      });
    }
  }

  backToContacts() {
    this.selectedChat = null;
  }

  get userPosts() {
    return this.userPostsData.length > 0 ? this.userPostsData : this.posts.filter(post => post.author === this.profileName);
  }

  searchQuery = '';
  searchResults: any[] = [];
  isSearching = false;
  searchActiveTab = 'users';
  showFollowersList = false;
  showFollowingList = false;
  followersList: User[] = [];
  followingList: User[] = [];
  suggestedUsers: any[] = [];
  showSuggestions = true;
  showDeleteConfirm = false;
  postToDelete: any = null;
  showDeleteCommentConfirm = false;
  commentToDelete: { post: any, commentId: number } | null = null;
  notifications: Notification[] = [];
  unreadNotificationCount = 0;
  
  loadUserProfile() {
    if (this.currentUser?.username) {
      this.profileService.getProfile(this.currentUser.username).subscribe({
        next: (profile) => {
          this.userProfile = profile;
          this.followersCount = profile.followersCount || 0;
          this.followingCount = profile.followingCount || 0;
        },
        error: (error) => {
          console.error('Error loading user profile:', error);
        }
      });
      
      // Load user posts - only posts by this user
      this.profileService.getUserPosts(this.currentUser.username).subscribe({
        next: (posts) => {
          this.userPostsData = posts;
          console.log('Loaded', posts.length, 'posts for user', this.currentUser.username);
        },
        error: (error) => {
          console.error('Error loading user posts:', error);
          this.userPostsData = [];
        }
      });
    }
  }
  
  onSearchInput() {
    if (!this.searchQuery.trim()) {
      this.searchResults = [];
      return;
    }
    
    this.isSearching = true;
    this.performSearch();
  }
  
  performSearch() {
    const query = this.searchQuery.trim();
    if (!query) return;
    
    // Search both users and posts
    Promise.all([
      this.profileService.searchUsers(query).toPromise(),
      this.postService.searchPosts(query).toPromise()
    ]).then(([users, posts]) => {
      this.searchResults = [
        ...(users || []).map(user => ({ ...user, type: 'user' })),
        ...(posts || []).map(post => ({ ...post, type: 'post' }))
      ];
      this.isSearching = false;
    }).catch(error => {
      console.error('Search error:', error);
      this.searchResults = [];
      this.isSearching = false;
    });
  }

  deletePost(post: any) {
    this.posts = this.posts.filter(p => p.id !== post.id);
    this.feedService.deletePost(post.id);
  }

  canDeletePost(post: any): boolean {
    return post.author === this.profileName;
  }

  editingPost: any = null;
  editPostContent = '';

  editPost(post: any) {
    this.editingPost = post;
    this.editPostContent = post.content;
  }

  saveEditPost() {
    if (this.editPostContent.trim() && this.editingPost) {
      this.editingPost.content = this.editPostContent;
      this.feedService.updatePost(this.editingPost);
      this.cancelEditPost();
    }
  }

  cancelEditPost() {
    this.editingPost = null;
    this.editPostContent = '';
  }

  get postsCount() {
    return this.userPosts.length;
  }
  
  deleteUserPost(post: any) {
    this.postToDelete = post;
    this.showDeleteConfirm = true;
  }

  confirmDelete() {
    if (this.postToDelete) {
      const postId = this.postToDelete.id;
      
      // Immediately update UI
      this.userPostsData = this.userPostsData.filter(p => p.id !== postId);
      this.posts = this.posts.filter(p => p.id !== postId);
      this.showDeleteConfirm = false;
      this.postToDelete = null;
      
      // Then call backend
      this.postService.deletePost(postId).subscribe({
        next: () => {
          // Post deleted successfully
        },
        error: (error) => {
          this.loadFeeds();
          this.loadUserProfile();
        }
      });
    }
  }

  cancelDelete() {
    this.showDeleteConfirm = false;
    this.postToDelete = null;
  }

  showFollowers() {
    this.setProfileTab('followers');
  }

  showFollowing() {
    this.setProfileTab('following');
  }
  
  loadFollowers() {
    if (this.currentUser?.username) {
      console.log('Loading followers for user:', this.currentUser.username);
      this.profileService.getFollowers(this.currentUser.username).subscribe({
        next: (followers) => {
          console.log('Followers loaded successfully:', followers);
          this.followersList = followers;
        },
        error: (error) => {
          console.error('Error loading followers:', error);
          this.followersList = [];
        }
      });
    } else {
      console.log('No current user found for loading followers');
    }
  }
  
  loadFollowing() {
    if (this.currentUser?.username) {
      console.log('Loading following for user:', this.currentUser.username);
      this.profileService.getFollowing(this.currentUser.username).subscribe({
        next: (following) => {
          console.log('Following loaded successfully:', following);
          this.followingList = following;
        },
        error: (error) => {
          console.error('Error loading following:', error);
          this.followingList = [];
        }
      });
    } else {
      console.log('No current user found for loading following');
    }
  }

  hideUserLists() {
    this.showFollowersList = false;
    this.showFollowingList = false;
  }

  togglePrivacy() {
    if (this.userProfile) {
      const newPrivacySetting = !this.userProfile.isPrivate;
      this.profileService.updateProfile({ isPrivate: newPrivacySetting.toString() }).subscribe({
        next: (updatedUser) => {
          if (this.userProfile) {
            this.userProfile.isPrivate = newPrivacySetting;
          }
        },
        error: (error) => {
          console.error('Error updating privacy setting:', error);
        }
      });
    }
  }

  checkForMentions(content: string) {
    const mentionRegex = /@(\w+)/g;
    const mentions = content.match(mentionRegex);
    if (mentions) {
      // TODO: Implement mention notifications via backend API
      console.log('Mentions found:', mentions);
    }
  }

  loadNotifications() {
    console.log('🔔 Loading notifications for user:', this.currentUser?.username);
    
    if (!this.currentUser) {
      console.warn('⚠️ No current user found for loading notifications');
      this.notifications = [];
      this.unreadNotificationCount = 0;
      return;
    }
    
    this.notificationService.getNotifications(this.currentUser.id).subscribe({
      next: (notifications) => {
        console.log('✅ Notifications loaded:', notifications.length);
        this.notifications = notifications;
        
        this.notificationService.getUnreadCount(this.currentUser.id).subscribe({
          next: (count) => this.unreadNotificationCount = count,
          error: () => this.unreadNotificationCount = 0
        });
      },
      error: (error) => {
        console.warn('⚠️ Notification service unavailable (this is optional)');
        this.notifications = [];
        this.unreadNotificationCount = 0;
      }
    });
  }
  
  markNotificationAsRead(notification: Notification) {
    if (!notification.readStatus) {
      this.notificationService.markAsRead(notification.id).subscribe({
        next: () => {
          notification.readStatus = true;
          this.unreadNotificationCount = Math.max(0, this.unreadNotificationCount - 1);
        },
        error: (error) => {
          console.error('Error marking notification as read:', error);
        }
      });
    }
  }
  
  acceptFollowRequest(notification: Notification) {
    if (notification.followRequestId) {
      this.notificationService.acceptFollowRequest(notification.followRequestId).subscribe({
        next: () => {
          console.log('Follow request accepted successfully');
          // Remove notification from list immediately
          this.notifications = this.notifications.filter(n => n.id !== notification.id);
          this.unreadNotificationCount = Math.max(0, this.unreadNotificationCount - 1);
          // Add delay to ensure backend processing is complete
          setTimeout(() => {
            this.loadUserProfile();
            this.loadSuggestedUsers();
            if (this.showFollowersList) {
              this.loadFollowers();
            }
          }, 500);
        },
        error: (error) => {
          console.error('Error accepting follow request:', error);
        }
      });
    }
  }
  
  rejectFollowRequest(notification: Notification) {
    if (notification.followRequestId) {
      this.notificationService.rejectFollowRequest(notification.followRequestId).subscribe({
        next: () => {
          console.log('Follow request rejected successfully');
          // Remove notification from list immediately
          this.notifications = this.notifications.filter(n => n.id !== notification.id);
          this.unreadNotificationCount = Math.max(0, this.unreadNotificationCount - 1);
          this.loadUserProfile();
        },
        error: (error) => {
          console.error('Error rejecting follow request:', error);
        }
      });
    }
  }
  
  loadSuggestedUsers() {
    if (!this.currentUser) {
      this.suggestedUsers = [];
      return;
    }
    
    // Get following list first
    this.profileService.getFollowing(this.currentUser.username).subscribe({
      next: (following) => {
        const followingUsernames = following.map(u => u.username);
        
        this.profileService.getAllUsers().subscribe({
          next: (users) => {
            // Filter out current user and already followed users
            this.suggestedUsers = users.filter(user => 
              user.username !== this.currentUser?.username && 
              user.id !== this.currentUser?.id &&
              !followingUsernames.includes(user.username)
            ).slice(0, 5);
          },
          error: (error) => {
            console.error('Error loading suggested users:', error);
          }
        });
      },
      error: () => {
        // If following list fails, just show all users except current
        this.profileService.getAllUsers().subscribe({
          next: (users) => {
            this.suggestedUsers = users.filter(user => 
              user.username !== this.currentUser?.username && 
              user.id !== this.currentUser?.id
            ).slice(0, 5);
          }
        });
      }
    });
  }
  
  isVideo(url: string): boolean {
    if (!url) return false;
    return url.startsWith('data:video/') || url.includes('.mp4') || url.includes('.webm') || url.includes('.ogg') || url.includes('.mov') || (url.startsWith('blob:') && this.selectedFileType === 'video');
  }
  
  isImage(url: string): boolean {
    if (!url) return false;
    return url.startsWith('data:image/') || url.includes('.jpg') || url.includes('.jpeg') || url.includes('.png') || url.includes('.gif') || url.includes('.webp') || (url.startsWith('blob:') && this.selectedFileType === 'image');
  }
  
  removeFollower(follower: User) {
    console.log('Attempting to remove follower:', follower.username);
    this.profileService.removeFollower(follower.username).subscribe({
      next: (response) => {
        console.log('Follower removed successfully:', response.message);
        this.followersCount = Math.max(0, this.followersCount - 1);
        this.loadFollowers();
        setTimeout(() => this.loadUserProfile(), 500);
      },
      error: (error) => {
        console.error('Error removing follower:', error);
      }
    });
  }
  
  updateSuggestedUserStatus(username: string, status: string) {
    const suggestedUser = this.suggestedUsers.find(u => u.username === username);
    if (suggestedUser) {
      suggestedUser.followStatus = status;
    }
  }
  
  getUserResults() {
    return this.searchResults.filter(result => 
      result.type === 'user' && result.username !== this.currentUser?.username
    );
  }
  
  getPostResults() {
    return this.searchResults.filter(result => result.type === 'post');
  }
  
  setSearchTab(tab: string) {
    this.searchActiveTab = tab;
  }
  
  dismissNotification(notification: Notification) {
    this.notificationService.deleteNotification(notification.id).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(n => n.id !== notification.id);
        if (!notification.readStatus) {
          this.unreadNotificationCount = Math.max(0, this.unreadNotificationCount - 1);
        }
      },
      error: (error) => {
        console.error('Error deleting notification:', error);
        // Still remove from UI even if backend fails
        this.notifications = this.notifications.filter(n => n.id !== notification.id);
        if (!notification.readStatus) {
          this.unreadNotificationCount = Math.max(0, this.unreadNotificationCount - 1);
        }
      }
    });
  }
  
  handleNotificationClick(notification: any) {
    console.log('Notification clicked:', notification);
    this.markNotificationAsRead(notification);
    
    if (notification.type === 'MESSAGE') {
      // Extract username from message (e.g., "kranthi sent you a message")
      let username = notification.fromUsername;
      
      if (!username && notification.message) {
        const match = notification.message.match(/^(\w+)\s+sent you a message/);
        if (match) {
          username = match[1];
        }
      }
      
      if (username) {
        console.log('Opening chat with:', username);
        // Navigate to chat tab and open conversation
        this.setActiveTab('chat');
        setTimeout(() => {
          // Fetch user profile for the contact
          this.profileService.getProfile(username).subscribe({
            next: (profile) => {
              console.log('Profile loaded:', profile);
              this.selectedContactData = {
                username: profile.username,
                profilePicture: profile.profilePicture || ''
              };
              this.selectedChat = username;
              this.loadConversation(username);
              this.unreadCounts[username] = 0;
              
              // Add to contacts if not present
              const existingContact = this.contacts.find(c => 
                (typeof c === 'string' ? c : c.username) === username
              );
              if (!existingContact) {
                this.contacts.unshift({
                  username: profile.username,
                  profilePicture: profile.profilePicture || ''
                });
              }
            },
            error: (err) => {
              console.error('Error loading profile:', err);
              this.selectedContactData = { username: username, profilePicture: '' };
              this.selectedChat = username;
              this.loadConversation(username);
              this.unreadCounts[username] = 0;
            }
          });
        }, 300);
      }
    }
  }
  
  refreshUnreadCounts() {
    this.contacts.forEach(contact => {
      this.chatService.getUnreadCount(contact, this.currentUser?.id).subscribe({
        next: (count) => {
          this.unreadCounts[contact] = count;
        },
        error: (error) => {
          this.unreadCounts[contact] = 0;
        }
      });
    });
  }
  
  onChatSearchInput() {
    if (!this.chatSearchQuery.trim()) {
      this.chatSearchResults = [];
      return;
    }
    
    // Load following list if not already loaded
    if (this.followingList.length === 0) {
      this.loadFollowing();
    }
    
    console.log('Searching for:', this.chatSearchQuery);
    console.log('Following list:', this.followingList.length, 'users');
    
    // Search from following list (people you follow)
    this.chatSearchResults = this.followingList.filter(user => 
      user.username.toLowerCase().includes(this.chatSearchQuery.toLowerCase()) ||
      (user.bio && user.bio.toLowerCase().includes(this.chatSearchQuery.toLowerCase()))
    );
    
    console.log('Search results:', this.chatSearchResults.length, 'found');
  }
  
  loadFollowingForChat() {
    console.log('Loading following list for chat...');
    this.loadFollowing();
    // Show a sample search to demonstrate
    setTimeout(() => {
      if (this.followingList.length > 0) {
        this.chatSearchQuery = this.followingList[0].username.substring(0, 2);
        this.onChatSearchInput();
      }
    }, 1000);
  }
  
  startChat(user: any) {
    this.selectedChat = user.username;
    this.selectedContactData = user;
    this.chatSearchQuery = '';
    this.chatSearchResults = [];
    this.loadConversation(user.username);
    // Add to contacts if not already there
    const existingContact = this.contacts.find(c => 
      (typeof c === 'string' ? c : c.username) === user.username
    );
    if (!existingContact) {
      this.contacts.unshift({
        username: user.username,
        profilePicture: user.profilePicture || ''
      });
    }
  }
  
  loadChatContacts() {
    console.log('🔍 Loading chat contacts for user:', this.currentUser?.username);
    
    if (!this.currentUser) {
      console.error('❌ No current user found for loading chat contacts');
      return;
    }
    
    this.contacts = [];
    
    this.chatService.getChatContacts(this.currentUser.id).subscribe({
      next: (conversations) => {
        console.log('✅ Chat contacts loaded successfully:', conversations.length, 'contacts');
        
        const uniqueUsernames = new Set<string>();
        conversations.forEach(conv => {
          const otherUsername = conv.participantUsernames?.find((u: string) => u !== this.currentUser?.username);
          if (otherUsername) {
            uniqueUsernames.add(otherUsername);
            this.unreadCounts[otherUsername] = conv.unreadCount || 0;
          }
        });
        
        // Fetch profile data for each contact
        const usernames = Array.from(uniqueUsernames);
        const contactsMap = new Map<string, any>();
        
        usernames.forEach(username => {
          this.profileService.getProfile(username).subscribe({
            next: (profile) => {
              if (!contactsMap.has(username)) {
                contactsMap.set(username, {
                  username: profile.username,
                  profilePicture: profile.profilePicture || ''
                });
                this.contacts = Array.from(contactsMap.values());
              }
            },
            error: () => {
              if (!contactsMap.has(username)) {
                contactsMap.set(username, { username, profilePicture: '' });
                this.contacts = Array.from(contactsMap.values());
              }
            }
          });
        });
      },
      error: (error) => {
        console.error('❌ Error loading chat contacts:', error);
        this.contacts = [];
      }
    });
  }

  // New methods for enhanced UI functionality
  hashtagInput = '';
  showPollCreation = false;
  showLocationTag = false;
  pollOptions = ['', ''];
  locationTag = '';

  togglePollCreation() {
    this.showPollCreation = !this.showPollCreation;
    if (this.showPollCreation) {
      this.pollOptions = ['', ''];
    }
  }

  toggleLocationTag() {
    this.showLocationTag = !this.showLocationTag;
    if (!this.showLocationTag) {
      this.locationTag = '';
    }
  }

  addPollOption() {
    if (this.pollOptions.length < 4) {
      this.pollOptions.push('');
    }
  }

  removePollOption(index: number) {
    if (this.pollOptions.length > 2) {
      this.pollOptions.splice(index, 1);
    }
  }

  updatePollOption(index: number, event: any) {
    if (event?.target && index >= 0 && index < this.pollOptions.length) {
      this.pollOptions[index] = event.target.value;
    }
  }

  trackByIndex(index: number, item: any): number {
    return index;
  }

  getDefaultAvatar(): string {
    return 'https://ui-avatars.com/api/?name=User&background=14B8A6&color=fff&size=128';
  }

  getUserAvatar(user: any): string {
    if (user?.profilePicture && user.profilePicture !== 'assets/default.jpg') {
      return user.profilePicture;
    }
    const name = user?.username || user?.name || 'User';
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=14B8A6&color=fff&size=128`;
  }

  removeSelectedFile() {
    this.selectedFile = null;
    this.selectedFileType = '';
    this.selectedFilePreview = null;
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

  clearPost() {
    this.newPostContent = '';
    this.hashtagInput = '';
    this.removeSelectedFile();
    this.showPollCreation = false;
    this.showLocationTag = false;
    this.pollOptions = ['', ''];
    this.locationTag = '';
    this.postVisibility = 'public';
  }

  getNotificationIcon(type: string): string {
    switch (type) {
      case 'FOLLOW_REQUEST': return 'fa-user-plus';
      case 'FOLLOW': return 'fa-user-check';
      case 'LIKE': return 'fa-heart';
      case 'COMMENT': return 'fa-comment';
      case 'MENTION': return 'fa-at';
      case 'MESSAGE': return 'fa-envelope';
      default: return 'fa-bell';
    }
  }

  getNotificationIconBg(type: string): string {
    switch (type) {
      case 'FOLLOW_REQUEST': return 'rgba(139, 92, 246, 0.8)';
      case 'FOLLOW': return 'rgba(16, 185, 129, 0.8)';
      case 'LIKE': return 'rgba(239, 68, 68, 0.8)';
      case 'COMMENT': return 'rgba(74, 144, 226, 0.8)';
      case 'MENTION': return 'rgba(245, 158, 11, 0.8)';
      case 'MESSAGE': return 'rgba(139, 92, 246, 0.8)';
      default: return 'rgba(107, 114, 128, 0.8)';
    }
  }

  getNotificationTitle(type: string): string {
    switch (type) {
      case 'FOLLOW_REQUEST': return 'Follow Request';
      case 'FOLLOW': return 'New Follower';
      case 'LIKE': return 'Post Liked';
      case 'COMMENT': return 'New Comment';
      case 'MENTION': return 'You were mentioned';
      case 'MESSAGE': return 'New Message';
      default: return 'Notification';
    }
  }

  getTotalLikes(): number {
    return this.userPosts.reduce((total, post) => total + (post.likesCount || 0), 0);
  }

  getTotalComments(): number {
    return this.userPosts.reduce((total, post) => total + (post.commentsCount || 0), 0);
  }

  getTotalShares(): number {
    return this.userPosts.reduce((total, post) => total + (post.sharesCount || 0), 0);
  }

  // New profile tab functionality
  profileActiveTab = 'posts';

  setProfileTab(tab: string) {
    this.profileActiveTab = tab;
    if (tab === 'followers') {
      this.loadFollowers();
    } else if (tab === 'following') {
      this.loadFollowing();
    }
  }

  getRecentActivity(): any[] {
    const activities: any[] = [];
    
    this.userPosts.slice(0, 3).forEach(post => {
      activities.push({
        icon: 'fa-plus-circle',
        text: 'Posted a new update',
        date: post.createdDate
      });
    });
    
    const sorted = activities.sort((a: any, b: any) => new Date(b.date).getTime() - new Date(a.date).getTime());
    return sorted.slice(0, 5);
  }
}