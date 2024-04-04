"use client";
import { ModeToggle } from "../common/theme-switch";

import Link from "next/link";

import { Button } from "../ui/button";
import { useSession } from "next-auth/react";
import { Avatar } from "../ui/avatar";
import { AvatarImage } from "@radix-ui/react-avatar";
import UserProfile from "../common/user-profile";
import { cn } from "@/lib/utils";
import { useState } from "react";
import CartPop from "./cart-pop";
import { Home } from "lucide-react";
import {
  Menubar,
  MenubarContent,
  MenubarItem,
  MenubarMenu,
  MenubarSeparator,
  MenubarTrigger,
} from "../ui/menubar";

export const revaliate = 0;

export default function Nav() {
  const session = useSession();
  const [showProfile, setShowProfile] = useState(false);

  const authUser = session?.data?.user;

  const isUser = authUser?.role === "ROLE_USER";
  const isTrainer = authUser?.role === "ROLE_TRAINER";
  const isAdmin = authUser?.role === "ROLE_ADMIN";
  const isAdminOrTrainer = isAdmin || isTrainer;

  return (
    <nav
      className="min-h-10 flex items-center justify-between px-4 py-2 border-b sm:sticky top-0 bg-opacity-60 z-[99]
    w-full border-border/40 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 flex-wrap 2xl:border-l 2xl:border-r
    "
    >
      <div className="flex items-center justify-center gap-4 flex-wrap">
        <div className="mr-8">
          <Link href="/" className="font-bold hover:underline">
            <Home />
          </Link>
        </div>

        {isUser && (
          <Link href="/posts/approved" className="font-bold hover:underline">
            Posts
          </Link>
        )}
        {!isUser && (
          <Menubar>
            <MenubarMenu>
              <MenubarTrigger>Posts</MenubarTrigger>
              <MenubarContent>
                <MenubarItem className="flex items-center justify-center">
                  <Link href="/posts/approved" className="text-center w-full">
                    Approved
                  </Link>
                </MenubarItem>
                {isAdmin && (
                  <>
                    <MenubarSeparator />
                    <MenubarItem className="flex items-center justify-center">
                      <Link href="/admin/posts" className="text-center w-full">
                        All Posts
                      </Link>
                    </MenubarItem>
                  </>
                )}
                {isAdminOrTrainer && authUser && (
                  <>
                    <MenubarSeparator />
                    <MenubarItem className="flex items-center justify-center">
                      <Link
                        href={`/trainer/user/${authUser.id}/posts`}
                        className="text-center w-full"
                      >
                        Your Posts
                      </Link>
                    </MenubarItem>
                    <MenubarSeparator />
                    <MenubarItem className="flex items-center justify-center">
                      <Link
                        href={`/trainer/posts/create`}
                        className="text-center w-full"
                      >
                        Create Post
                      </Link>
                    </MenubarItem>
                  </>
                )}
              </MenubarContent>
            </MenubarMenu>
          </Menubar>
        )}
        {isAdminOrTrainer && (
          <Menubar>
            <MenubarMenu>
              <MenubarTrigger>Exercises</MenubarTrigger>
              <MenubarContent>
                <MenubarItem className="flex items-center justify-center">
                  <Link
                    href={`/trainer/user/${authUser?.id}/exercises`}
                    className="text-center w-full"
                  >
                    Your Exercises
                  </Link>
                </MenubarItem>
                <MenubarSeparator />
                <MenubarItem className="flex items-center justify-center">
                  <Link
                    href={`/trainer/exercises/create`}
                    className="text-center w-full"
                  >
                    Create Exercise
                  </Link>
                </MenubarItem>
                {isAdmin && (
                  <>
                    <MenubarSeparator />
                    <MenubarItem className="flex items-center justify-center">
                      <Link
                        href="/admin/exercises"
                        className="text-center w-full"
                      >
                        All Exercises
                      </Link>
                    </MenubarItem>
                  </>
                )}
              </MenubarContent>
            </MenubarMenu>
          </Menubar>
        )}
        {isUser && (
          <Link
            href="/trainings/approved"
            className="font-bold hover:underline"
          >
            Trainings
          </Link>
        )}
        {!isUser && (
          <Menubar>
            <MenubarMenu>
              <MenubarTrigger>Trainings</MenubarTrigger>
              <MenubarContent>
                <MenubarItem className="flex items-center justify-center">
                  <Link
                    href={`/trainings/approved`}
                    className="text-center w-full"
                  >
                    Trainings Approved
                  </Link>
                </MenubarItem>
                <MenubarSeparator />
                <MenubarItem className="flex items-center justify-center">
                  <Link
                    href={`/trainer/user/${authUser?.id}/trainings`}
                    className="text-center w-full"
                  >
                    Your Trainings
                  </Link>
                </MenubarItem>
                <MenubarSeparator />
                <MenubarItem className="flex items-center justify-center">
                  <Link
                    href={`/trainer/trainings/create`}
                    className="text-center w-full"
                  >
                    Create Training
                  </Link>
                </MenubarItem>
                {isAdmin && (
                  <>
                    <MenubarSeparator />
                    <MenubarItem className="flex items-center justify-center">
                      <Link
                        href="/admin/trainings"
                        className="text-center w-full"
                      >
                        All Trainings
                      </Link>
                    </MenubarItem>
                  </>
                )}
              </MenubarContent>
            </MenubarMenu>
          </Menubar>
        )}
        {!isAdmin && (
          <Link
            href={`/users/${authUser?.id}/orders`}
            className="font-bold hover:underline"
          >
            Orders
          </Link>
        )}
        {isAdmin && (
          <Menubar>
            <MenubarMenu>
              <MenubarTrigger>Orders</MenubarTrigger>
              <MenubarContent>
                <MenubarItem className="flex items-center justify-center">
                  <Link
                    href={`/users/${authUser?.id}/orders`}
                    className="text-center w-full"
                  >
                    Your Orders
                  </Link>
                </MenubarItem>
                <MenubarSeparator />
                <MenubarItem className="flex items-center justify-center">
                  <Link href={`/admin/orders`} className="text-center w-full">
                    All Orders
                  </Link>
                </MenubarItem>
              </MenubarContent>
            </MenubarMenu>
          </Menubar>
        )}
        {isAdmin && (
          <Link href="/admin" className="font-bold hover:underline">
            Admin
          </Link>
        )}
      </div>

      <div
        className="mx-auto md:ml-auto md:mr-1 flex items-center justify-center gap-6
      mt-2 sm:mt-0
      "
      >
        {authUser && (
          <>
            <CartPop userId={authUser.id} />

            <div className="flex items-center justify-center gap-2 w-full md:w-fit flex-wrap">
              <div className="flex items-center justify-center">
                {authUser?.image ? (
                  <Avatar
                    className="cursor-pointer"
                    onClick={() => setShowProfile((prev) => !prev)}
                  >
                    <AvatarImage src={authUser?.image} alt={authUser?.email} />
                  </Avatar>
                ) : (
                  authUser?.email
                )}
              </div>
              <div
                className={cn(
                  "transition-all ease-in-out duration-300 absolute right-0  z-50",
                  "transform origin-top bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 top-[100%] overflow-hidden shadow-2xl",
                  {
                    "max-h-0": !showProfile,
                    "max-h-[500px] ": showProfile,
                  }
                )}
              >
                {<UserProfile user={authUser} />}
              </div>
              <Link
                href="/auth/signout"
                className="font-bold ml-3 hover:underline"
              >
                Sign Out
              </Link>
            </div>
          </>
        )}
        <ModeToggle />
      </div>
    </nav>
  );
}
