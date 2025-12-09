import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeedComponent } from './feed.component';

const routes: Routes = [
  { path: '', component: FeedComponent }
];

@NgModule({
  imports: [
    FeedComponent,
    RouterModule.forChild(routes)
  ]
})
export class FeedModule { }
