# konessem
NES emulator written in Kotlin from scratch.

## Demo

- [Falling](https://github.com/vblank182/falling-nes)

<img src="https://user-images.githubusercontent.com/59043/103191859-dfa3dc00-4919-11eb-86bf-d68afd4afc8b.gif" alt="falling.gif" width="320"/>

- [TkShoot](http://hp.vector.co.jp/authors/VA042397/nes/sample.html)

<img src="https://user-images.githubusercontent.com/59043/103191876-f21e1580-4919-11eb-8c54-9de815c3bdde.gif" alt="tkshoot.gif" width="320"/>

I believe you can play Super Mario Bros on Konessem :)

## How to try this?

- Prepare NES file
- Run Konessem and choose the NES file

  ```
  ./gradlew run
  ```

## Controls

- Up: `W`
- Down: `S`
- Left: `A`
- Right: `D`
- A: `K`
- B: `L`
- Select: `,`
- Start: `.`

## Unsupported features

- APU
- Accurate cycle calculation
- Other non-volatile memory
- Large CHR ROM (> 8KB)
- 2nd keypad
- [Unofficial opcodes](https://wiki.nesdev.com/w/index.php/Programming_with_unofficial_opcodes) (Added some codes, but I don't think they correctly work)
