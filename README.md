# Mobile Week 2

## Features
- Tip Calculator (keeps state on rotation)
- 3-screen prototype with bottom navigation (Tip, List, About)

## Accessibility fixes
1. Added contentDescription to images
2. Increased tap targets to 48 dp
3. Used sp for text sizes

## Lifecycle observation
Rotating the phone calls onPause, onStop, onDestroy, then onCreate, onStart, onResume.
