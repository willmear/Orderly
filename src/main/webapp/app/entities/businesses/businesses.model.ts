export interface IBusinesses {
  id: number;
  name?: string | null;
  summary?: string | null;
  rating?: number | null;
  rateCount: number | null;
  location?: string | null;
  type?: string | null;
  image?: string | null;
  imageContentType?: string | null;
  owner: string;
}

export type NewBusinesses = Omit<IBusinesses, 'id'> & { id: null };
