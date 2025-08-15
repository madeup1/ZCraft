# Docs

### Performance optimizations:

One performance optimization I am incorporating is extremely safe
multi threading to allow for faster computation of actions.

Resources that are shared across threads should use `QueueLock` and no matter what, call `QueueLock::queue()` even if the resource is free.

If called when the resource is free, it executes it immediately, if not, it waits until it is free to do so.