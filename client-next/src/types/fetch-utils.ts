export type Role = "ROLE_USER" | "ROLE_TRAINER" | "ROLE_ADMIN";
export type SortDirection = "asc" | "desc" | "none";
export const sortDirections: SortDirection[] = ["asc", "desc", "none"] as const;
