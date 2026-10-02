import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';

import { FormControl, ReactiveFormsModule } from '@angular/forms';

import { RouterLink } from '@angular/router';

import { debounceTime, distinctUntilChanged, filter, of, switchMap } from 'rxjs';

import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { PokemonSearchResult } from '../../models/pokemon';

import { PokemonService } from '../../services/pokemon.service';

@Component({
  selector: 'app-pokemon-list',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './pokemon-list.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PokemonListComponent {
  private readonly pokemonService = inject(PokemonService);

  private readonly destroyRef = inject(DestroyRef);

  readonly searchControl = new FormControl('', {
    nonNullable: true,
  });

  readonly results = signal<PokemonSearchResult[]>([]);

  readonly loading = signal(false);

  readonly error = signal<string | null>(null);

  constructor() {
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),

        switchMap((search) => {
          const normalized = search.trim();

          if (normalized.length < 2) {
            this.results.set([]);
            this.loading.set(false);

            return of([] as PokemonSearchResult[]);
          }

          this.loading.set(true);
          this.error.set(null);

          return this.pokemonService.search(normalized);
        }),

        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (results) => {
          this.results.set(results);
          this.loading.set(false);
        },

        error: () => {
          this.error.set('Pokémon konnten nicht geladen werden.');

          this.results.set([]);
          this.loading.set(false);
        },
      });
  }
}
