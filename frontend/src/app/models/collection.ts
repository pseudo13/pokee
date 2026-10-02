export interface CollectionItem {
  id: number;
  pokemonId: number;
  externalId: number;
  name: string;
  imageUrl: string | null;
  rating: number;
  favorite: boolean;
  notes: string | null;
  addedAt: string;
}

export interface AddCollectionItemRequest {
  pokemonId: number;
  rating: number;
  favorite: boolean;
  notes: string;
}

export interface UpdateCollectionItemRequest {
  rating?: number;
  favorite?: boolean;
  notes?: string;
}
