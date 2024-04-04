import { Role } from "@/types/fetch-utils";
import { Session } from "next-auth";
import { JWT } from "next-auth/jwt";

declare module "next-auth" {
  interface User {
    firstName: string;
    lastName: string;
    email: string;
    token: string;
    role: Role;
    image: string;
    error?: string;
  }

  interface Session {
    user?: User & {
      firstName: string;
      lastName: string;
      email: string;
      token: string;
      role: Role;
      image: string;
      // error?: string;
    };
    jwt?: string;
  }
}

declare module "next-auth/jwt" {
  interface JWT {
    firstName?: string;
    lastName?: string;
    email?: string;
    token?: string;
    role?: Role;
    user?: User;
    image?: string;
    error?: string;
  }
}
