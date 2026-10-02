import { Routes } from '@angular/router';

import { PokemonListComponent } from './pages/pokemon-list/pokemon-list';

import { PokemonDetailComponent } from './pages/pokemon-detail/pokemon-detail';

import { LoginComponent } from './pages/login/login';

import { RegisterComponent } from './pages/register/register';
import { CollectionComponent } from './pages/collection/collection';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'pokemon',
    pathMatch: 'full',
  },

  {
    path: 'login',
    component: LoginComponent,
  },

  {
    path: 'register',
    component: RegisterComponent,
  },

  {
    path: 'pokemon',
    component: PokemonListComponent,
  },

  {
    path: 'pokemon/:externalId',
    component: PokemonDetailComponent,
  },

  {
    path: 'collection',
    component: CollectionComponent,
    canActivate: [authGuard],
  },
];
