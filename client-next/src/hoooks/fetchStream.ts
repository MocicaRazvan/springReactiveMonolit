import ndjsonStream from "can-ndjson-stream";
import { BaseError } from "@/types/responses";

export interface FetchStreamProps<T, E> {
  path: string;
  method?: "GET" | "POST" | "PUT" | "DELETE" | "HEAD" | "PATCH";
  body?: object | null;
  customHeaders?: HeadersInit;
  queryParams?: Record<string, string>;
  arrayQueryParam?: Record<string, string[]>;
  token?: string;
  cache?: RequestCache;
  aboveController?: AbortController;
  successCallback?: (data: T) => void;
}

export async function fetchStream<T = any, E extends BaseError = BaseError>({
  path,
  method = "GET",
  body = null,
  customHeaders = {},
  queryParams = {},
  token = "",
  arrayQueryParam = {},
  cache = "default",
  aboveController,
  successCallback,
}: FetchStreamProps<T, E>) {
  let messages: T[] = [];
  let error: E | null = null;
  let isFinished = false;

  const abortController = aboveController || new AbortController();

  const headers = new Headers(customHeaders);
  headers.set("Accept", "application/x-ndjson");

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  if (body !== null && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  const querySearch = new URLSearchParams(queryParams).toString();

  const arrayQueryStrings = Object.entries(arrayQueryParam)
    .map(([key, values]) => {
      return values
        .map(
          (value) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`
        )
        .join("&");
    })
    .join("&");

  const combinedQuery = [querySearch, arrayQueryStrings]
    .filter((part) => part)
    .join("&");

  const fetchOptions: RequestInit = {
    method,
    headers,
    signal: abortController.signal,
    cache,
  };

  if (body !== null && method !== "GET" && method !== "HEAD") {
    fetchOptions.body = JSON.stringify(body);
  }

  const url = combinedQuery ? `${path}?${combinedQuery}` : path;

  try {
    const res = await fetch(`http://localhost:8080${url}`, fetchOptions);
    const stream = ndjsonStream<T, E>(res.body);
    const reader = stream.getReader();

    const read = async (): Promise<void> => {
      const { done, value } = await reader.read();
      if (done) {
        isFinished = true;
        return;
      }
      if (!res.ok) {
        error = value as E;
      } else {
        messages = [...messages, value as T];
        successCallback?.(value as T);
      }
      await read();
    };

    await read();
  } catch (err) {
    error = err as E;

    isFinished = true;
  }

  const cleanUp = () => {
    if (!abortController.signal.aborted) {
      abortController.abort();
    }
  };

  return { messages, error, isFinished, cleanUp };
}
