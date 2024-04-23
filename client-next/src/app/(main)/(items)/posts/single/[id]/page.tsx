"use client";

import Loader from "@/components/ui/spinner";
import useFetchStream from "@/hoooks/useFetchStream";
import {
  CustomEntityModel,
  PostResponse,
  ResponseWithUserDtoEntity,
} from "@/types/dto";
import { BaseError } from "@/types/responses";
import { useSession } from "next-auth/react";
import { notFound, useParams, useRouter } from "next/navigation";

import { fetchStream } from "@/hoooks/fetchStream";
import { Suspense, useCallback, useEffect, useState } from "react";

import CustomImageCarousel from "@/components/common/custom-image-crousel";
import LikesDislikes from "@/components/common/single/likes-dislikes";
import PostComments from "./comments";
import { Avatar } from "@radix-ui/react-avatar";
import { AvatarImage } from "@/components/ui/avatar";
import Link from "next/link";
import AlertDialogDeletePost from "@/components/dialogs/posts/delete-post";
import { useGetPost } from "@/app/(main)/(items)/posts/single/hook";
import { Button } from "@/components/ui/button";

export default function SinglePost() {
  // const [postState, setPostState] = useState<PostResponse | null>(null);
  // const { id } = useParams();
  // const session = useSession();
  // const router = useRouter();
  //
  // const authUser = session.data?.user;
  // const { messages, error } = useFetchStream<
  //   ResponseWithUserDtoEntity<PostResponse>,
  //   BaseError
  // >({ path: `/posts/withUser/${id}`, method: "GET", authToken: true });
  //
  // console.log(messages);
  //
  // const post = messages[0]?.model?.content;
  // const user = messages[0]?.user.content;
  const {
    postState,
    setPostState,
    messages,
    error,
    authUser,
    post,
    user,
    router,
    session,
  } = useGetPost();

  const react = useCallback(
    async (type: "like" | "dislike") => {
      if (!post?.id || !session.data?.user?.token) return;
      try {
        const resp = await fetchStream<CustomEntityModel<PostResponse>>({
          path: `/posts/${type}/${post.id}`,
          method: "PATCH",
          token: session.data?.user?.token,
        });
        console.log(resp);
        const newPost = resp.messages[0]?.content;
        setPostState((prev) =>
          !prev
            ? prev
            : {
                ...prev,
                userLikes: newPost.userLikes,
                userDislikes: newPost.userDislikes,
              },
        );
      } catch (error) {
        console.log(error);
      }
    },
    [post?.id, session.data?.user?.token, setPostState],
  );
  // useEffect(() => {
  //   if (messages.length > 0) {
  //     setPostState(messages[0]?.model?.content);
  //   }
  // }, [JSON.stringify(messages)]);

  console.log(error);

  if (error?.status) {
    notFound();
  }

  if (!post || !authUser)
    return (
      <section className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all">
        <Loader />
      </section>
    );

  const isAdmin = authUser.role === "ROLE_ADMIN";
  const isOwner = postState?.userId === parseInt(authUser.id);
  const isOwnerOrAdmin = isOwner || isAdmin;

  if (!post.approved && !isOwnerOrAdmin) {
    notFound();
  }

  const isLiked = postState?.userLikes.includes(parseInt(authUser.id));
  const isDisliked = postState?.userDislikes.includes(parseInt(authUser.id));

  return (
    <section className="w-full  min-h-[calc(100vh-4rem)] flex-col items-center justify-center transition-all px-6 py-10 relative ">
      {post?.approved && (
        <div className="w-40 absolute top-10 left-[270px] flex items-center justify-center gap-4">
          <LikesDislikes
            react={react}
            likes={postState?.userLikes || []}
            dislikes={postState?.userDislikes || []}
            isLiked={isLiked || false}
            isDisliked={isDisliked || false}
          />
        </div>
      )}
      <h1 className="text-4xl bold text-center mb-20">{postState?.title}</h1>
      {!post.approved && (
        <h2 className="absolute top-10 right-[270px] text-2xl font-bold text-center bold text-destructive">
          Not Approved
        </h2>
      )}
      {post?.images.length > 0 && <CustomImageCarousel images={post?.images} />}
      <div className="mt-20 px-14">
        <div
          className="prose max-w-none [&_ol]:list-decimal [&_ul]:list-disc dark:prose-invert text-wrap"
          dangerouslySetInnerHTML={{ __html: postState?.body ?? "" }}
        />
      </div>
      <Link href={`/users/${user.id}`}>
        <div className="flex items-center justify-center mt-16 gap-2 cursor-pointer px-4 py-2 hover:border rounded-lg transition-all w-1/3 mx-auto group">
          <Avatar className="group-hover:translate-y-[-10px]">
            <AvatarImage
              src={user.image}
              alt={user.email}
              className="w-16 h-16"
            />
          </Avatar>
          <p className="group-hover:translate-y-[-10px]">{user.email}</p>
        </div>
      </Link>
      <Suspense fallback={<Loader className="w-full h-full" />}>
        <PostComments postId={post.id} />
      </Suspense>
      {isOwnerOrAdmin && (
        <div className="sticky bottom-3  flex items-center justify-center gap-4">
          <AlertDialogDeletePost
            post={post}
            token={session.data?.user?.token}
            callBack={() => {
              isAdmin
                ? router.push("/admin/posts")
                : router.push(`/trainer/user/${authUser.id}/posts`);
            }}
          />
          {isOwner && (
            <Button
              onClick={() => {
                router.push(
                  `/trainer/user/${authUser.id}/posts/update/${post.id}`,
                );
              }}
            >
              Update Post
            </Button>
          )}
        </div>
      )}
    </section>
  );
}
