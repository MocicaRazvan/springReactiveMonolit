"use client";

import { useCartForUser } from "@/context/cart-context";
import { Button } from "../ui/button";
import { ShoppingCartIcon, Trash2 } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "../ui/dropdown-menu";
import { TrainingResponse } from "@/types/dto";
import { Fragment } from "react";
import { ScrollArea } from "../ui/scroll-area";
import { cn } from "@/lib/utils";
import { useRouter } from "next/navigation";
import { useToast } from "../ui/use-toast";
import { ToastAction } from "../ui/toast";
import Link from "next/link";

interface Props {
  userId: string;
}

export default function CartPop({ userId }: Props) {
  const {
    usersCart,
    removeFromCartForUser,
    usersCartTotalPrice,
    addToCartForUser,
  } = useCartForUser(userId);
  const router = useRouter();
  const { toast } = useToast();

  //   const trainings: TrainingResponse[] = [
  //     {
  //       id: 22,

  //       createdAt: "2024-03-21T06:08:00.540549",

  //       updatedAt: "2024-03-21T06:08:00.540549",

  //       userId: 1,

  //       body: "TTTTTTTTTTTTTTTTTTTTTTTTTIt is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).",

  //       title: "Back ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/pm2ed9ebkgjygiyfjfpf.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/a0yxzqwtepejbncqe88f.png",
  //       ],

  //       approved: false,

  //       price: 99,

  //       exercises: [1, 2],
  //     },
  //     {
  //       id: 22,

  //       createdAt: "2024-03-21T06:08:00.540549",

  //       updatedAt: "2024-03-21T06:08:00.540549",

  //       userId: 1,

  //       body: "TTTTTTTTTTTTTTTTTTTTTTTTTIt is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).",

  //       title: "Back ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/pm2ed9ebkgjygiyfjfpf.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/a0yxzqwtepejbncqe88f.png",
  //       ],

  //       approved: false,

  //       price: 99,

  //       exercises: [1, 2],
  //     },
  //     {
  //       id: 22,

  //       createdAt: "2024-03-21T06:08:00.540549",

  //       updatedAt: "2024-03-21T06:08:00.540549",

  //       userId: 1,

  //       body: "TTTTTTTTTTTTTTTTTTTTTTTTTIt is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).",

  //       title: "Back ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/pm2ed9ebkgjygiyfjfpf.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/a0yxzqwtepejbncqe88f.png",
  //       ],

  //       approved: false,

  //       price: 99,

  //       exercises: [1, 2],
  //     },
  //     {
  //       id: 22,

  //       createdAt: "2024-03-21T06:08:00.540549",

  //       updatedAt: "2024-03-21T06:08:00.540549",

  //       userId: 1,

  //       body: "TTTTTTTTTTTTTTTTTTTTTTTTTIt is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).",

  //       title: "Back ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/pm2ed9ebkgjygiyfjfpf.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/a0yxzqwtepejbncqe88f.png",
  //       ],

  //       approved: false,

  //       price: 99,

  //       exercises: [1, 2],
  //     },
  //     {
  //       id: 22,

  //       createdAt: "2024-03-21T06:08:00.540549",

  //       updatedAt: "2024-03-21T06:08:00.540549",

  //       userId: 1,

  //       body: "TTTTTTTTTTTTTTTTTTTTTTTTTIt is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).",

  //       title: "Back ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/pm2ed9ebkgjygiyfjfpf.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/a0yxzqwtepejbncqe88f.png",
  //       ],

  //       approved: false,

  //       price: 99,

  //       exercises: [1, 2],
  //     },
  //     {
  //       id: 22,

  //       createdAt: "2024-03-21T06:08:00.540549",

  //       updatedAt: "2024-03-21T06:08:00.540549",

  //       userId: 1,

  //       body: "TTTTTTTTTTTTTTTTTTTTTTTTTIt is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).",

  //       title: "Back ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/pm2ed9ebkgjygiyfjfpf.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/a0yxzqwtepejbncqe88f.png",
  //       ],

  //       approved: false,

  //       price: 99,

  //       exercises: [1, 2],
  //     },
  //     {
  //       id: 22,

  //       createdAt: "2024-03-21T06:08:00.540549",

  //       updatedAt: "2024-03-21T06:08:00.540549",

  //       userId: 1,

  //       body: "TTTTTTTTTTTTTTTTTTTTTTTTTIt is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).",

  //       title: "Back ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/pm2ed9ebkgjygiyfjfpf.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710994073/a0yxzqwtepejbncqe88f.png",
  //       ],

  //       approved: false,

  //       price: 99,

  //       exercises: [1, 2],
  //     },

  //     {
  //       id: 21,

  //       createdAt: "2024-03-21T05:37:07.967183",

  //       updatedAt: "2024-03-21T05:37:07.967183",

  //       userId: 1,

  //       body: "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by ac",

  //       title: "Chest day",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710992219/ednzdawgg5j8gns4l22m.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710992219/fp0qtwcrkppd91y6zxm8.png",
  //       ],

  //       approved: true,

  //       price: 11,

  //       exercises: [1],
  //     },

  //     {
  //       id: 14,

  //       createdAt: "2024-03-21T05:36:42.308202",

  //       updatedAt: "2024-03-21T05:36:42.308202",

  //       userId: 1,

  //       body: "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by ac",

  //       title: "Chest day ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991844/dmaxxzgy0ef7vuwewebj.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991845/ioyofslqo2jmpccpl5ry.png",
  //       ],

  //       approved: false,

  //       price: 8,

  //       exercises: [1],
  //     },

  //     {
  //       id: 15,

  //       createdAt: "2024-03-21T05:36:42.784604",

  //       updatedAt: "2024-03-21T05:36:42.784604",

  //       userId: 1,

  //       body: "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by ac",

  //       title: "Chest day ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991844/dmaxxzgy0ef7vuwewebj.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991845/ioyofslqo2jmpccpl5ry.png",
  //       ],

  //       approved: false,

  //       price: 8,

  //       exercises: [1],
  //     },

  //     {
  //       id: 16,

  //       createdAt: "2024-03-21T05:36:46.047333",

  //       updatedAt: "2024-03-21T05:36:46.047333",

  //       userId: 1,

  //       body: "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by ac",

  //       title: "Chest day ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991844/dmaxxzgy0ef7vuwewebj.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991845/ioyofslqo2jmpccpl5ry.png",
  //       ],

  //       approved: false,

  //       price: 8,

  //       exercises: [1],
  //     },

  //     {
  //       id: 17,

  //       createdAt: "2024-03-21T05:36:46.51722",

  //       updatedAt: "2024-03-21T05:36:46.51722",

  //       userId: 1,

  //       body: "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by ac",

  //       title: "Chest day ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991844/dmaxxzgy0ef7vuwewebj.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991845/ioyofslqo2jmpccpl5ry.png",
  //       ],

  //       approved: false,

  //       price: 8,

  //       exercises: [1],
  //     },

  //     {
  //       id: 18,

  //       createdAt: "2024-03-21T05:36:46.685519",

  //       updatedAt: "2024-03-21T05:36:46.685519",

  //       userId: 1,

  //       body: "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by ac",

  //       title: "Chest day ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991844/dmaxxzgy0ef7vuwewebj.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991845/ioyofslqo2jmpccpl5ry.png",
  //       ],

  //       approved: false,

  //       price: 8,

  //       exercises: [1],
  //     },

  //     {
  //       id: 19,

  //       createdAt: "2024-03-21T05:36:46.86378",

  //       updatedAt: "2024-03-21T05:36:46.86378",

  //       userId: 1,

  //       body: "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by ac",

  //       title: "Chest day ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991844/dmaxxzgy0ef7vuwewebj.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991845/ioyofslqo2jmpccpl5ry.png",
  //       ],

  //       approved: false,

  //       price: 8,

  //       exercises: [1],
  //     },

  //     {
  //       id: 20,

  //       createdAt: "2024-03-21T05:36:47.106976",

  //       updatedAt: "2024-03-21T05:36:47.106976",

  //       userId: 1,

  //       body: "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by ac",

  //       title: "Chest day ",

  //       userDislikes: [],

  //       userLikes: [],

  //       images: [
  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991844/dmaxxzgy0ef7vuwewebj.jpg",

  //         "http://res.cloudinary.com/lamatutorial/image/upload/v1710991845/ioyofslqo2jmpccpl5ry.png",
  //       ],

  //       approved: false,

  //       price: 8,

  //       exercises: [1],
  //     },

  //     {
  //       id: 1,

  //       createdAt: "2024-03-13T05:03:08.15652",

  //       updatedAt: "2024-03-11T05:03:08.15652",

  //       userId: 1,

  //       body: "Sample training description",

  //       title: "Sample Training",

  //       userDislikes: [3, 4],

  //       userLikes: [1, 2],

  //       images: [],

  //       approved: true,

  //       price: 99.99,

  //       exercises: [1],
  //     },
  //   ];

  return (
    <DropdownMenu modal={false}>
      <DropdownMenuTrigger asChild>
        <div className="relative">
          <Button variant="outline" disabled={usersCart.total === 0}>
            <ShoppingCartIcon />
          </Button>
          {usersCart.total > 0 && (
            <div className="absolute top-[-2px] right-[-10px] rounded-full w-7 h-7 bg-destructive flex items-center justify-center">
              <p>{usersCart.total}</p>
            </div>
          )}
        </div>
      </DropdownMenuTrigger>
      {usersCart.total > 0 && (
        <DropdownMenuContent className="w-52">
          <DropdownMenuGroup>
            <ScrollArea
              className={cn(
                "w-full px-1",
                usersCart.total < 5
                  ? `h-[calc(${usersCart.total}rem+3.5rem)]`
                  : "h-60"
              )}
            >
              {usersCart.trainings.map((training, index) => (
                <Fragment key={training.id + index}>
                  <DropdownMenuItem onClick={(e) => e.preventDefault()}>
                    <div className="py-1 h-10 flex items-center justify-between w-full">
                      <div>
                        <Link
                          href={`/trainings/single/${
                            training.id
                          }?exercises=${training.exercises.join(`,`)}`}
                          className="hover:underline"
                        >
                          <p className="text-lg mb-1">
                            {training.title.length > 6
                              ? training.title.substring(0, 6) + "..."
                              : training.title}
                          </p>
                        </Link>
                        <p className="font-bold">${training.price}</p>
                      </div>
                      <Button
                        variant="destructive"
                        className="py-1 px-2"
                        onClick={() => {
                          removeFromCartForUser({ id: training.id });
                          toast({
                            title: training.title,
                            description: "Removed From Cart",
                            variant: "destructive",
                            action: (
                              <ToastAction
                                altText="Undo"
                                onClick={() => addToCartForUser(training)}
                              >
                                Undo
                              </ToastAction>
                            ),
                          });
                        }}
                      >
                        <Trash2 className="w-4 h-16" />
                      </Button>
                    </div>
                  </DropdownMenuItem>

                  <DropdownMenuSeparator />
                </Fragment>
              ))}
              <div className="h-12 mt-2 px-1 py-2 flex items-center justify-between">
                <p className="font-bold text-center">
                  Total: <span className="ml-1">${usersCartTotalPrice}</span>
                </p>
                <Button
                  variant="default"
                  className="py-1 px-2"
                  onClick={() => {
                    router.push("/cart");
                  }}
                >
                  Order
                </Button>
              </div>
            </ScrollArea>
          </DropdownMenuGroup>
        </DropdownMenuContent>
      )}
    </DropdownMenu>
  );
}
