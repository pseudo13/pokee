import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { CollectionService } from '../../services/collection.service';
import { CollectionItem, UpdateCollectionItemRequest } from '../../models/collection';

@Component({
  selector: 'app-collection',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './collection.html',
})
export class CollectionComponent implements OnInit {
  private readonly collectionService = inject(CollectionService);

  readonly items = signal<CollectionItem[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  readonly editingId = signal<number | null>(null);
  readonly editRating = signal(5);
  readonly editFavorite = signal(false);
  readonly editNotes = signal('');

  readonly saving = signal(false);
  readonly deletingId = signal<number | null>(null);

  ngOnInit(): void {
    this.loadCollection();
  }

  loadCollection(): void {
    this.loading.set(true);
    this.error.set(null);

    this.collectionService.getCollection().subscribe({
      next: (items) => {
        this.items.set(items);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load your collection.');
        this.loading.set(false);
      },
    });
  }

  startEdit(item: CollectionItem): void {
    this.editingId.set(item.id);
    this.editRating.set(item.rating);
    this.editFavorite.set(item.favorite);
    this.editNotes.set(item.notes ?? '');
  }

  cancelEdit(): void {
    this.editingId.set(null);
    this.saving.set(false);
  }

  setRating(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.editRating.set(Number(select.value));
  }

  setFavorite(event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    this.editFavorite.set(checkbox.checked);
  }

  setNotes(event: Event): void {
    const textarea = event.target as HTMLTextAreaElement;
    this.editNotes.set(textarea.value);
  }

  saveEdit(item: CollectionItem): void {
    if (this.saving()) {
      return;
    }

    const request: UpdateCollectionItemRequest = {
      rating: this.editRating(),
      favorite: this.editFavorite(),
      notes: this.editNotes(),
    };

    this.saving.set(true);
    this.error.set(null);

    this.collectionService.update(item.id, request).subscribe({
      next: (updatedItem) => {
        this.items.update((items) =>
          items.map((currentItem) =>
            currentItem.id === updatedItem.id ? updatedItem : currentItem,
          ),
        );

        this.editingId.set(null);
        this.saving.set(false);
      },
      error: () => {
        this.error.set('Could not save the changes.');
        this.saving.set(false);
      },
    });
  }

  remove(item: CollectionItem): void {
    if (this.deletingId() !== null) {
      return;
    }

    this.deletingId.set(item.id);
    this.error.set(null);

    this.collectionService.delete(item.id).subscribe({
      next: () => {
        this.items.update((items) => items.filter((currentItem) => currentItem.id !== item.id));

        if (this.editingId() === item.id) {
          this.cancelEdit();
        }

        this.deletingId.set(null);
      },
      error: () => {
        this.error.set('Could not remove the Pokémon.');
        this.deletingId.set(null);
      },
    });
  }
}
