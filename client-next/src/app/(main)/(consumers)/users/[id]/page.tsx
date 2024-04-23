"use client";

import { Badge } from "@/components/ui/badge";
import Loader from "@/components/ui/spinner";
import useFetchStream from "@/hoooks/useFetchStream";
import { CustomEntityModel, UserDto } from "@/types/dto";
import { BaseError } from "@/types/responses";
import { useSession } from "next-auth/react";
import { useParams, useRouter } from "next/navigation";
import { UpdateAccordion } from "./update-accordion";
import { useEffect, useState } from "react";
import { AlertDialogMakeTrainer } from "./make-trainer-alert";
import { Avatar, AvatarImage } from "@radix-ui/react-avatar";
import { AvatarFallback } from "@/components/ui/avatar";

interface UserPageProps {}

export default function UserPage({}: UserPageProps) {
  const [stateUser, setStateUser] = useState<UserDto>();
  const [refresh, setRefresh] = useState(false);
  const { id } = useParams();
  const { messages } = useFetchStream<CustomEntityModel<UserDto>, BaseError>({
    path: `/users/${id}`,
    method: "GET",
    authToken: true,
  });
  const session = useSession();
  const authUser = session.data?.user;
  const user = messages[0]?.content;

  const isOwner = authUser?.email === user?.email;
  const isUser = user?.role === "ROLE_USER";
  const isAuthAdmin = authUser?.role === "ROLE_ADMIN";

  useEffect(() => {
    if (user) {
      setStateUser(user);
    }
  }, [JSON.stringify(messages)]);

  console.log(user);

  if (!user || !session.data?.user)
    return (
      <section className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all">
        <Loader />
      </section>
    );
  return (
    <section className="w-full min-h-[calc(100vh-4rem)] flex items-center justify-center transition-all mt-4">
      <div className=" w-full mx-2 md:mx-0 md:w-1/2  border rounded-xl px-6 py-8">
        <div className="flex items-center justify-center gap-4">
          {stateUser?.image && (
            <Avatar>
              <AvatarImage
                src={stateUser?.image || ""}
                alt={stateUser?.email}
                className="w-16 h-16"
              />
              {/* <AvatarFallback>{stateUser?.email}</AvatarFallback> */}
            </Avatar>
          )}
          <h3 className="text-xl font-bold text-center">{stateUser?.email}</h3>
        </div>
        <div className="flex justify-between w-2/3 mx-auto items-center mt-10">
          <div className="flex flex-col items-center justify-center gap-2 w-full">
            <p className="text-lg w-full">
              First Name:{" "}
              <span className="font-bold ml-4">{stateUser?.firstName}</span>
            </p>
            <p className="text-lg w-full">
              Last Name:{" "}
              <span className="font-bold ml-4">{stateUser?.lastName}</span>
            </p>
          </div>

          <Badge
            variant={
              stateUser?.role === "ROLE_ADMIN"
                ? "destructive"
                : stateUser?.role === "ROLE_TRAINER"
                  ? "default"
                  : "secondary"
            }
            className="text-lg"
          >
            {stateUser?.role}
          </Badge>
        </div>
        <div className="flex items-center justify-center mt-8 w-full">
          {/* make it not a dialog, not working with cloudinary!!!!!!! */}
          {isAuthAdmin && isUser && (
            <AlertDialogMakeTrainer
              user={user}
              token={session?.data?.user?.token}
              setStateUser={setStateUser}
            />
          )}
        </div>
        {isOwner && (
          <UpdateAccordion
            user={user}
            token={session?.data?.user?.token}
            setStateUser={setStateUser}
          />
        )}
      </div>
    </section>
  );
}
