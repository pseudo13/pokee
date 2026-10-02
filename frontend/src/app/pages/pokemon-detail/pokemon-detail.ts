import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PokemonService } from '../../services/pokemon.service';
import { CollectionService } from '../../services/collection.service';
import { AuthService } from '../../services/auth.service';

import { Pokemon } from '../../models/pokemon';

@Component({
  selector: 'app-pokemon-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './pokemon-detail.html',
})
export class PokemonDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly pokemonService = inject(PokemonService);
  private readonly collectionService = inject(CollectionService);
  readonly authService = inject(AuthService);

  readonly pokemon = signal<Pokemon | null>(null);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  readonly adding = signal(false);
  readonly added = signal(false);
  readonly addError = signal<string | null>(null);

  ngOnInit(): void {
    const externalId = Number(this.route.snapshot.paramMap.get('externalId'));

    if (!externalId) {
      this.error.set('Invalid Pokémon ID.');
      this.loading.set(false);
      return;
    }

    this.loadPokemon(externalId);
  }

  private loadPokemon(externalId: number): void {
    this.loading.set(true);
    this.error.set(null);

    this.pokemonService.getByExternalId(externalId).subscribe({
      next: (pokemon) => {
        this.pokemon.set(pokemon);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Pokémon could not be loaded.');
        this.loading.set(false);
      },
    });
  }

  addToCollection(): void {
    const pokemon = this.pokemon();

    if (!pokemon || this.adding() || this.added()) {
      return;
    }

    this.adding.set(true);
    this.addError.set(null);

    this.collectionService
      .add({
        pokemonId: pokemon.id,
        rating: 5,
        favorite: false,
        notes: '',
      })
      .subscribe({
        next: () => {
          this.adding.set(false);
          this.added.set(true);
        },
        error: (error) => {
          this.adding.set(false);

          if (error.status === 409) {
            this.addError.set('This Pokémon is already in your collection.');
          } else {
            this.addError.set('Could not add this Pokémon to your collection.');
          }
        },
      });
  }
}
