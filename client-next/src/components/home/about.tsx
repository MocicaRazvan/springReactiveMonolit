"use client";

import { AnimatedText } from "../ui/animated-text";

export default function HomeTitle() {
  const text =
    "Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum nesciunt ea repudiandae amet cupiditate nihil commodi hic non totam autem iusto voluptas, quae accusamus cumque id, similique est quod asperiores.; Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum nesciunt ea repudiandae amet cupiditate nihil commodi hic non totam autem iusto voluptas, quae accusamus cumque id, similique est quod asperiores.; Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum nesciunt ea repudiandae amet cupiditate nihil commodi hic non totam autem iusto voluptas, quae accusamus cumque id, similique est quod asperiores.;Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum nesciunt ea repudiandae amet cupiditate nihil commodi hic non totam autem iusto voluptas, quae accusamus cumque id, similique est quod asperiores.;Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum nesciunt ea repudiandae amet cupiditate nihil commodi hic non totam autem iusto voluptas, quae accusamus cumque id, similique est quod asperiores.;Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum nesciunt ea repudiandae amet cupiditate nihil commodi hic non totam autem iusto voluptas, quae accusamus cumque id, similique est quod asperiores.;Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum nesciunt ea repudiandae amet cupiditate nihil commodi hic non totam autem iusto voluptas, quae accusamus cumque id, similique est quod asperiores.;Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum nesciunt ea repudiandae amet cupiditate nihil commodi hic non totam autem iusto voluptas, quae accusamus cumque id, similique est quod asperiores.;Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum nesciunt ea repudiandae amet cupiditate nihil commodi hic non totam autem iusto voluptas, quae accusamus cumque id, similique est quod asperiores.;Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum nesciunt ea repudiandae amet cupiditate nihil commodi hic non totam autem iusto voluptas, quae accusamus cumque id, similique est quod asperiores.;Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum nesciunt ea repudiandae amet cupiditate nihil commodi hic non totam autem iusto voluptas, quae accusamus cumque id, similique est quod asperiores.;";

  return (
    <div className="space-y-20 mt-20">
      <AnimatedText
        text="Why to chose us..."
        className="text-[4rem] tracking-tighter font-bold text-center"
        // once={true}
        animation={{
          hidden: { opacity: 0, y: 20 },
          visible: { opacity: 1, y: 0, transition: { staggerChildren: 0.1 } },
        }}
      />
      <div className=" px-20">
        <AnimatedText
          text={text}
          // once={true}
          className="text-center text-2xl leading-7 "
          animation={{
            hidden: { opacity: 0 },
            visible: { opacity: 1, transition: { duration: 1 } },
          }}
          split={false}
          amount={0.35}
        />
      </div>
    </div>
  );
}
