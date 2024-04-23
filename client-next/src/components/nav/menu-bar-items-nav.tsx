"use client";

import { Role } from "@/types/fetch-utils";
import { Session } from "next-auth";
import { MenubarItem, MenubarSeparator } from "@/components/ui/menubar";
import Link from "next/link";
import { Fragment, memo, useCallback, useMemo } from "react";
import { isDeepEqual } from "@/lib/utils";
import { LinkNav, shouldRenderLink } from "@/components/nav/links";
import {
  DropdownMenuItem,
  DropdownMenuSeparator,
} from "@/components/ui/dropdown-menu";

interface MenuBarItemsNavProps {
  links: LinkNav[];
  authUser: NonNullable<Session["user"]>;
  isDropdown?: boolean;
}

const MenuBarItemsNav = memo<MenuBarItemsNavProps>(
  ({ links, authUser, isDropdown = false }) => {
    const shouldRenderNavLink = useCallback(
      (linkRole: Role) => shouldRenderLink(authUser, linkRole),
      [authUser],
    );

    const Item = useMemo(
      () => (isDropdown ? DropdownMenuItem : MenubarItem),
      [isDropdown],
    );
    const Separator = useMemo(
      () => (isDropdown ? DropdownMenuSeparator : MenubarSeparator),
      [isDropdown],
    );

    return (
      <>
        {links.map(({ text, href, role }, i) => {
          return (
            shouldRenderNavLink(role) && (
              <Fragment key={href}>
                <Item className="flex items-center justify-center">
                  <Link href={href} className={"text-start w-full"}>
                    {text}
                  </Link>
                </Item>
                {i < links.length - 1 && <Separator />}
              </Fragment>
            )
          );
        })}
      </>
    );
  },
  (prevProps, nextProps) =>
    isDeepEqual(prevProps.authUser, nextProps.authUser) &&
    prevProps.links.every((link, i) => isDeepEqual(link, nextProps.links[i])),
);

MenuBarItemsNav.displayName = "MenuBarItemsNav";
export { MenuBarItemsNav };
