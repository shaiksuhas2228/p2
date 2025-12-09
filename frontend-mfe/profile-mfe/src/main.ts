import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { ProfileModule } from './app/profile/profile.module';

platformBrowserDynamic().bootstrapModule(ProfileModule)
  .catch(err => console.error(err));
