import { ModeToggle } from "@/components/common/theme-switch";
import { Button } from "@/components/ui/button";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Home, Menu } from "lucide-react";
import Link from "next/link";

export default function SheetNav() {
  return (
    <Sheet>
      <SheetTrigger asChild>
        <Button className="sticky top-4 left-6 z-50">
          <Menu />
        </Button>
      </SheetTrigger>
      <SheetContent className="w-[200px] sm:w-[270px]" side={"left"}>
        <SheetHeader>
          <SheetTitle className="text-center text-2xl">Admin</SheetTitle>
        </SheetHeader>
        <div className="flex flex-col items-center justify-center w-full mt-20 gap-4 sm:gap-7">
          <Button asChild variant="outline" className="sm:text-lg">
            <Link href="/">
              <Home className="inline-block mr-2" /> <p>Go to main site</p>
            </Link>
          </Button>
          <Button asChild variant="outline" className="sm:text-lg w-36">
            <Link href="/admin/users">Users</Link>
          </Button>
          <Button asChild variant="outline" className="sm:text-lg w-36">
            <Link href="/admin/posts">Posts</Link>
          </Button>
          <Button asChild variant="outline" className="sm:text-lg w-36">
            <Link href="/admin/exercises">Exercises</Link>
          </Button>
          <Button asChild variant="outline" className="sm:text-lg w-36">
            <Link href="/admin/training">Training</Link>
          </Button>
          <Button asChild variant="outline" className="sm:text-lg w-36">
            <Link href="/admin/orders">Orders</Link>
          </Button>
          <ModeToggle />
        </div>
      </SheetContent>
    </Sheet>
  );
}
