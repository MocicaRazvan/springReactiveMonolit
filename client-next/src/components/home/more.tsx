"use client";

import Image from "next/image";
import picture6 from "../../../public/header/Picture6.jpg";
import { useEffect, useRef, useState } from "react";
import { useScroll, useTransform, motion } from "framer-motion";
import { Dumbbell } from "lucide-react";
import { Button } from "../ui/button";
import { useRouter } from "next/navigation";

export default function MoreHome() {
  const [isMounted, setIsMounted] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);
  const container = useRef(null);
  const router = useRouter();
  const { scrollYProgress } = useScroll({
    target: container,
    offset: ["start end", "end start"],
  });

  const ctY = useTransform(scrollYProgress, [0, 1], ["0%", "-60%"]);
  const bgY = useTransform(scrollYProgress, [0, 1], ["-10%", "30%"]);

  if (!isMounted) return null;
  return (
    <>
      <div
        ref={container}
        className="w-full mt-20 overflow-hidden grid place-items-center relative h-[75vh]"
      >
        <motion.div
          className="relative z-10 mx-auto w-1/2 text-4xl text-center"
          style={{ y: ctY }}
          initial={{ opacity: 0, scale: 0 }}
          whileInView={{ opacity: 1, scale: 1 }}
          // while in view instead of aniamte to repeat the animation
          transition={{
            type: "spring",
            stiffness: 85,
            delay: 0.125,
            duration: 0.75,
            damping: 10,
            repeatType: "mirror",
          }}
        >
          <Button
            variant="default"
            className="text-2xl px-5 py-3"
            onClick={() => {
              router.push("/auth/signin");
            }}
          >
            Don&apos;t wait, start now!
          </Button>
          <p className="mt-6 italic font-bold ">
            Be part of a strong community{" "}
            <Dumbbell
              size={34}
              className="inline ml-1 text-destructive font-bold"
            />
          </p>
        </motion.div>
        <motion.div
          className="absolute inset-0 h-full grid place-items-center"
          style={{ y: bgY }}
        >
          <div className="w-full h-full">
            <Image src={picture6} layout="fill" objectFit="cover" alt="more" />
          </div>
        </motion.div>
      </div>
      {/* <div className="h-[1000px]"></div> */}
    </>
  );
}
