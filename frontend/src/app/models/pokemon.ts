export interface PokemonSearchResult {
  externalId: number;
  name: string;
}

export interface Pokemon {
  id: number;
  externalId: number;
  name: string;
  imageUrl: string | null;
  height: number | null;
  weight: number | null;
}

