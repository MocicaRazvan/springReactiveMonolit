"use client";

//https://www.frontend.fyi/v/staggered-text-animations-with-framer-motion

import { motion, useAnimation, useInView, Variant } from "framer-motion";
import { useEffect, useRef } from "react";
type AnimatedTextProps = {
  text: string | string[];
  className?: string;
  once?: boolean;
  repeatDelay?: number;
  animation?: {
    hidden: Variant;
    visible: Variant;
  };
  split?: boolean;
  amount?: number;
};

const defaultAnimations = {
  hidden: {
    opacity: 0,
    y: 20,
  },
  visible: {
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.1,
    },
  },
};

export const AnimatedText = ({
  text,
  className = "",
  once,
  repeatDelay,
  animation = defaultAnimations,
  split = true,
  amount = 0.5,
}: AnimatedTextProps) => {
  const controls = useAnimation();
  const textArray = Array.isArray(text) ? text : [text];
  const ref = useRef<HTMLDivElement>(null);
  const isInView = useInView(ref, { amount, once });

  useEffect(() => {
    let timeout: NodeJS.Timeout;
    const show = () => {
      controls.start("visible");
      if (repeatDelay) {
        timeout = setTimeout(async () => {
          await controls.start("hidden");
          controls.start("visible");
        }, repeatDelay);
      }
    };

    if (isInView) {
      show();
    } else {
      controls.start("hidden");
    }

    return () => clearTimeout(timeout);
  }, [isInView]);

  const renderText = () => {
    if (!split) {
      return (
        <motion.span variants={animation}>{textArray.join(" ")}</motion.span>
      );
    }

    return textArray.map((line, lineIndex) => (
      <span className="block" key={`${line}-${lineIndex}`}>
        {line.split(" ").map((word, wordIndex) => (
          <span className="inline-block" key={`${word}-${wordIndex}`}>
            {word.split("").map((char, charIndex) => (
              <motion.span
                key={`${char}-${charIndex}`}
                className="inline-block"
                variants={animation}
              >
                {char}
              </motion.span>
            ))}
            {wordIndex < line.split(" ").length - 1 ? (
              <span className="inline-block">&nbsp;</span>
            ) : null}
          </span>
        ))}
      </span>
    ));
  };

  return (
    <div className={className} ref={ref}>
      <motion.span
        initial="hidden"
        animate={controls}
        variants={{
          visible: { transition: { staggerChildren: split ? 0.1 : undefined } },
          hidden: {},
        }}
        aria-hidden="true"
      >
        {renderText()}
      </motion.span>
    </div>
  );
};
