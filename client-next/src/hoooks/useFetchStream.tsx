import { useCallback, useEffect, useRef, useState } from "react";
import { useSession } from "next-auth/react";
import ndjsonStream from "can-ndjson-stream";
import { BaseError } from "@/types/responses";
import { fetchStream, FetchStreamProps } from "./fetchStream";

export interface UseFetchStreamProps<T, E> {
  path: string;
  method?: "GET" | "POST" | "PUT" | "DELETE" | "HEAD" | "PATCH";
  body?: object | null;
  authToken?: boolean;
  customHeaders?: HeadersInit;
  queryParams?: Record<string, string>;
  arrayQueryParam?: Record<string, string[]>;
  cache?: RequestCache;
}

interface UseFetchStreamReturn<T, E> {
  messages: T[];
  error: E | null;
  isFinished: boolean;
  refetch: () => void;
}

// export function useFetchStream<T = any, E extends BaseError = BaseError>({
//   path,
//   method = "GET",
//   body = null,
//   authToken = false,
//   customHeaders = {},
//   queryParams = {},
//   cache = "no-cache",
//   arrayQueryParam = {},
// }: UseFetchStreamProps<T, E>): UseFetchStreamReturn<T, E> {
//   const [messages, setMessages] = useState<T[]>([]);
//   const [error, setError] = useState<E | null>(null);
//   const [isFinished, setIsFinished] = useState<boolean>(false);
//   const [refetchState, setRefetchState] = useState(false);
//   const { data: session } = useSession();

//   const refetch = useCallback(() => {
//     setRefetchState((prevIndex) => !prevIndex);
//   }, []);

//   useEffect(() => {
//     //if (messages.length > 0) {
//     //  setMessages([]);
//     // }
//     setMessages([]);
//     setError(null);
//     setIsFinished(false);

//     const abortController = new AbortController();
//     const token = authToken && session?.user?.token ? session.user.token : null;

//     if (authToken && !token) {
//       return;
//     }

//     const headers = new Headers(customHeaders);
//     headers.set("Accept", "application/x-ndjson");

//     if (token) {
//       headers.set("Authorization", `Bearer ${token}`);
//     }

//     if (body !== null && !headers.has("Content-Type")) {
//       headers.set("Content-Type", "application/json");
//     }

//     const querySearch = new URLSearchParams(queryParams).toString();

//     const arrayQueryStrings = Object.entries(arrayQueryParam)
//       .map(([key, values]) => {
//         return values
//           .map(
//             (value) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`
//           )
//           .join("&");
//       })
//       .join("&");

//     const combinedQuery = [querySearch, arrayQueryStrings]
//       .filter((part) => part)
//       .join("&");

//     const fetchOptions: RequestInit = {
//       method,
//       headers,
//       signal: abortController.signal,
//       cache,
//     };

//     if (body !== null && method !== "GET" && method !== "HEAD") {
//       fetchOptions.body = JSON.stringify(body);
//     }

//     const url = combinedQuery ? `${path}?${combinedQuery}` : path;
//     fetch(
//       `http://localhost:8080` + url,

//       fetchOptions
//     )
//       .then((res) => {
//         const stream = ndjsonStream<T, E>(res.body);
//         const reader = stream.getReader();
//         const read = async (): Promise<void> => {
//           const { done, value } = await reader.read();
//           if (done) {
//             setIsFinished(true);
//             return;
//           }
//           if (res.ok === false) {
//             setError(value as E);
//           } else {
//             setMessages((prev: T[]) => [...prev, value as T]);
//           }
//           return read();
//         };
//         return read();
//       })

//       .catch((err) => {
//         console.log(err);
//         if (err.name !== "AbortError") {
//           setError((e) => ({ ...e, message: err.message } as E));
//         }
//         setIsFinished(true);
//       });
//     return () => {
//       abortController.abort();
//     };

//     // eslint-disable-next-line react-hooks/exhaustive-deps
//   }, [
//     path,
//     method,
//     JSON.stringify(body),
//     authToken,
//     JSON.stringify(session?.user?.token),
//     JSON.stringify(customHeaders),
//     JSON.stringify(queryParams),
//     refetchState,
//   ]);

//   return { messages, error, isFinished, refetch };
// }

export function useFetchStream<T = any, E extends BaseError = BaseError>({
  path,
  method = "GET",
  body = null,
  authToken = false,
  customHeaders = {},
  queryParams = {},
  cache = "no-cache",
  arrayQueryParam = {},
}: UseFetchStreamProps<T, E>): UseFetchStreamReturn<T, E> {
  const [messages, setMessages] = useState<T[]>([]);
  const [error, setError] = useState<E | null>(null);
  const [isFinished, setIsFinished] = useState<boolean>(false);
  const [refetchState, setRefetchState] = useState(false);
  const { data: session } = useSession();
  const refetch = useCallback(() => {
    setRefetchState((prevIndex) => !prevIndex);
  }, []);
  useEffect(() => {
    setMessages([]);
    setError(null);
    setIsFinished(false);
    if (authToken && !session?.user?.token) {
      return () => {
        console.log("No token");
      };
    }
    const token = authToken && session?.user?.token ? session.user.token : "";
    const abortController = new AbortController();

    const fetchProps: FetchStreamProps<T, E> = {
      path,
      method,
      body,
      customHeaders,
      queryParams,
      arrayQueryParam,
      token,
      cache,
      aboveController: abortController,
      successCallback: (data) => {
        setMessages((prev) => [...prev, data]);
      },
    };

    fetchStream<T, E>(fetchProps)
      .then(({ error, isFinished }) => {
        setError(error);
        setIsFinished(isFinished);
      })
      .catch((err) => {
        console.log(err);
        if (err instanceof Object && "message" in err) {
          setError(err as E);
        }
        setIsFinished(true);
      });

    return () => {
      try {
        if (abortController && !abortController.signal.aborted)
          abortController.abort();
      } catch (e) {
        console.log(e);
      }
    };
  }, [
    path,
    method,
    JSON.stringify(body),
    authToken,
    JSON.stringify(session?.user?.token),
    JSON.stringify(customHeaders),
    JSON.stringify(queryParams),
    JSON.stringify(arrayQueryParam),
    refetchState,
  ]);

  console.log(messages.length);
  return { messages, error, isFinished, refetch };
}

export default useFetchStream;
