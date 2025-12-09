import { Component, Input, OnInit, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-avatar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './avatar.component.html',
  styleUrl: './avatar.component.css'
})
export class AvatarComponent implements OnInit, OnChanges {
  @Input() src?: string;
  @Input() name?: string;
  @Input() size: number = 40;
  
  hasValidImage = false;

  ngOnInit() {
    this.checkImage();
  }

  ngOnChanges() {
    this.checkImage();
  }

  checkImage() {
    if (this.src && this.src.trim() !== '' && this.src !== 'default.jpg' && this.src !== 'assets/default.jpg' && !this.src.includes('default')) {
      this.hasValidImage = true;
    } else {
      this.hasValidImage = false;
    }
  }

  onImageError() {
    this.hasValidImage = false;
  }

  getInitial(): string {
    if (this.name && this.name.trim() !== '') {
      return this.name.charAt(0).toUpperCase();
    }
    return '?';
  }

  getAvatarColor(): string {
    const colors = [
      'linear-gradient(135deg, #14B8A6, #0D9488)',
      'linear-gradient(135deg, #F97316, #EA580C)',
      'linear-gradient(135deg, #8B5CF6, #7C3AED)',
      'linear-gradient(135deg, #EC4899, #DB2777)',
      'linear-gradient(135deg, #3B82F6, #2563EB)',
      'linear-gradient(135deg, #10B981, #059669)',
      'linear-gradient(135deg, #F59E0B, #D97706)',
      'linear-gradient(135deg, #6366F1, #4F46E5)'
    ];
    
    const name = this.name || 'User';
    const charCode = name.charCodeAt(0);
    return colors[charCode % colors.length];
  }
}