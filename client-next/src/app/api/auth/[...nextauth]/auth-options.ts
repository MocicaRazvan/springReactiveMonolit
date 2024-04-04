import { NextAuthOptions } from "next-auth";
import CredentialsProvider from "next-auth/providers/credentials";

export const authOptions: NextAuthOptions = {
  providers: [
    CredentialsProvider({
      name: "Credentials",
      credentials: {
        email: {
          label: "Email",
          type: "email",
          placeholder: "johndoe@gmail.com",
        },
        password: { label: "Password", type: "password" },
      },
      async authorize(credentials, req) {
        try {
          if (!credentials?.email || !credentials?.password) {
            return { error: "Missing credentials" };
          }

          const resp = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            body: JSON.stringify(credentials),
            headers: { "Content-Type": "application/json" },
          });

          if (!resp.ok) {
            return { error: "CredentailsSingin" };
          }

          const user = await resp.json();

          console.log(user);

          if (!user) {
            return { error: "Invalid credentials" };
          }

          return user;
        } catch (error) {
          return { error: "An error occurred" };
        }
      },
    }),
  ],
  callbacks: {
    async jwt({ token, user, trigger, session }) {
      if (trigger === "update") {
        token.user = session.data.user;
        return token;
      }
      if (user) {
        token.user = user;
      }
      return token;
    },
    async session({ session, token }) {
      if (token.user) {
        session.user = token.user;
      }
      return session;
    },
    async signIn({ user }) {
      if (user?.error) {
        throw new Error(user.error);
      }
      return true;
    },
  },
  pages: {
    signIn: "/auth/signin",
    signOut: "/auth/signout",
  },
};
