import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { FeedModule } from './app/feed/feed.module';

platformBrowserDynamic().bootstrapModule(FeedModule)
  .catch(err => console.error(err));
