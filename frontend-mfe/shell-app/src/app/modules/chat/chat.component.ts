import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, ChatMessage } from '../../core/services/chat.service';
import { AuthService } from '../../core/services/auth.service';
import { ProfileService } from '../../core/services/profile.service';
import { AvatarComponent } from '../../shared/components/avatar.component';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, AvatarComponent],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent implements OnInit {
  contacts: any[] = [];
  selectedChat: string | null = null;
  messages: { [key: string]: ChatMessage[] } = {};
  newMessage = '';
  currentUser: any = null;
  isLoading = true;
  searchQuery = '';
  searchResults: any[] = [];
  followingList: any[] = [];

  constructor(
    private chatService: ChatService,
    private authService: AuthService,
    private profileService: ProfileService
  ) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    this.loadChatContacts();
    this.loadFollowing();
  }

  loadChatContacts() {
    this.chatService.getChatContacts(this.currentUser?.id).subscribe({
      next: (conversations) => {
        const uniqueContacts = new Set<string>();
        conversations.forEach((conv: any) => {
          const otherUsername = conv.participantUsernames?.find((u: string) => u !== this.currentUser?.username);
          if (otherUsername) uniqueContacts.add(otherUsername);
        });
        this.contacts = Array.from(uniqueContacts);
        this.loadContactProfiles();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading chat contacts:', error);
        this.isLoading = false;
      }
    });
  }

  loadContactProfiles() {
    this.contacts.forEach(username => {
      this.profileService.getProfile(username).subscribe({
        next: (profile) => {
          const index = this.contacts.indexOf(username);
          if (index !== -1) {
            this.contacts[index] = {
              username: profile.username,
              profilePicture: profile.profilePicture || ''
            };
          }
        },
        error: (err) => console.error('Error loading profile:', err)
      });
    });
  }

  loadFollowing() {
    if (this.currentUser?.username) {
      this.profileService.getFollowing(this.currentUser.username).subscribe({
        next: (following) => {
          this.followingList = following.map(user => ({
            ...user,
            profilePicture: user.profilePicture || ''
          }));
        },
        error: (error) => {
          console.error('Error loading following:', error);
        }
      });
    }
  }

  onSearchInput() {
    if (!this.searchQuery.trim()) {
      this.searchResults = [];
      return;
    }
    
    this.searchResults = this.followingList.filter(user => 
      user.username.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  startChat(user: any) {
    this.selectedChat = user.username;
    this.searchQuery = '';
    this.searchResults = [];
    this.loadConversation(user.username);
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

  selectChat(contact: any) {
    this.selectedChat = typeof contact === 'string' ? contact : contact.username;
    this.loadConversation(this.selectedChat);
  }

  loadConversation(username: string) {
    this.chatService.getConversation(username).subscribe({
      next: (messages) => {
        this.messages[username] = messages;
      },
      error: (error) => {
        console.error('Error loading conversation:', error);
        this.messages[username] = [];
      }
    });
  }

  sendMessage() {
    if (this.newMessage.trim() && this.selectedChat) {
      this.chatService.sendMessage(this.selectedChat, this.newMessage.trim()).subscribe({
        next: (message) => {
          if (!this.messages[this.selectedChat!]) {
            this.messages[this.selectedChat!] = [];
          }
          this.messages[this.selectedChat!].push(message);
          this.newMessage = '';
        },
        error: (error) => {
          console.error('Error sending message:', error);
        }
      });
    }
  }

  backToContacts() {
    this.selectedChat = null;
  }

  getSelectedContactPicture(): string {
    if (!this.selectedChat) return '';
    const contact = this.contacts.find(c => 
      (typeof c === 'string' ? c : c.username) === this.selectedChat
    );
    return contact && typeof contact !== 'string' ? contact.profilePicture || '' : '';
  }
}