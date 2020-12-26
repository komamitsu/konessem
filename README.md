# konessem
NES emulator written in Kotlin from scratch.

## Demo

- [Falling](https://github.com/vblank182/falling-nes)
- [TkShoot](http://hp.vector.co.jp/authors/VA042397/nes/sample.html)

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